package services.io.reception.mapping;

import java.lang.reflect.Field;
import java.util.Map;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import akka.actor.ActorRef;
import akka.actor.Props;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.project.instance.Project;
import models.laboratory.reception.instance.AbstractFieldConfiguration;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.instance.SampleHelper;
import play.Logger;
import play.Play;
import play.libs.Akka;
import rules.services.RulesActor6;
import rules.services.RulesMessage;
import services.io.reception.Mapping;
import validation.ContextValidation;
import validation.utils.ValidationConstants;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult.Sort;

public class SampleMapping extends Mapping<Sample> {
	
	private static ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor6.class));
	
	/**
	 * 
	 * @param objects : list of all db objects need by type samples, supports, containers
	 * @param configuration : the filed configuration for the current type
	 * @param action
	 * @param contextValidation
	 */
	public SampleMapping(Map<String, Map<String, DBObject>> objects, Map<String, ? extends AbstractFieldConfiguration> configuration, Action action, ContextValidation contextValidation) {
		super(objects, configuration, action, InstanceConstants.SAMPLE_COLL_NAME, Sample.class, Mapping.Keys.sample, contextValidation);
	}
	
	/**
	 * convert a file line in Sample
	 * we override the defaut comportment to reused a prexist sample.
	 * @param rowMap
	 * @return
	 */
	public Sample convertToDBObject(Map<Integer, String> rowMap) throws Exception{
		Sample object = type.newInstance();
		boolean needPopulate = false;
		if(Action.update.equals(action)){
			object = get(object, rowMap, true);
			needPopulate=true;
		}else if(Action.save.equals(action)){
			Sample objectInDB = get(object, rowMap, false);
			if(null != objectInDB){
				object = objectInDB;
				needPopulate=false;
			}else if(object.code != null){
				Sample objectInObjects = (Sample)objects.get(Mapping.Keys.sample.toString()).get(object.code);
				if(null != objectInObjects){
					object = objectInObjects;
				}
				needPopulate=true;
			}else{
				needPopulate=true;
			}
		}
		
		if(null != object && needPopulate){
			Field[] fields = type.getFields();
			for(Field field : fields){
				populateField(field, object, rowMap);			
			}
			update(object);
			
		}
		
		return object;
	}
	
	
	protected void update(Sample sample) {
		
		if(Action.update.equals(action)){
			sample.traceInformation.setTraceInformation(contextValidation.getUser());
		}else{
			sample.traceInformation = new TraceInformation(contextValidation.getUser());
		}
		//update categoryCode by default.
		sample.categoryCode = SampleType.find.findByCode(sample.typeCode).category.code;
		
		if(sample.life != null && sample.life.from != null && sample.life.from.sampleCode != null){
			Sample parentSample = MongoDBDAO.findOne(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("code",sample.life.from.sampleCode).in("projectCodes", sample.life.from.projectCode));
			if(null != parentSample){
				sample.life.from.sampleTypeCode=parentSample.typeCode;
				if(null != parentSample.life && null != parentSample.life.path){
					sample.life.path=parentSample.life.path+","+parentSample.code;
				}else{
					sample.life.path=","+parentSample.code;
				}
				//force this information 
				sample.properties.putAll(parentSample.properties);	
				if(!parentSample.taxonCode.equals(sample.taxonCode)){
					contextValidation.addErrors("taxonCode","error.receptionfile.taxonCode.diff", sample.taxonCode, parentSample.taxonCode);
				}
				if(!parentSample.referenceCollab.equals(sample.referenceCollab)){
					contextValidation.addErrors("referenceCollab","error.receptionfile.referenceCollab.diff", sample.referenceCollab, parentSample.referenceCollab);
				}				
			}else{
				contextValidation.addErrors("sample", ValidationConstants.ERROR_NOTEXISTS_MSG, sample.life.from.projectCode+" + "+sample.life.from.sampleCode);
			}
		}else{
			sample.life = null;
		}
		
		//Call rules to add properties to sample
		Logger.debug("sample "+sample);
		SampleHelper.executeRules(sample, "sampleCreation");
		Logger.debug("sample "+sample);
	}


	@Override
	public void consolidate(Sample sample) {
		
	}	
	
	@Override
	public void synchronizeMongoDB(DBObject c){
		if(Action.save.equals(action)){
			Sample sample = (Sample)c;
			CodeHelper.getInstance().updateProjectSampleCodeIfNeeded(sample.projectCodes.iterator().next(), sample.code);
		}
		super.synchronizeMongoDB(c);
	}
	@Override
	public void rollbackInMongoDB(DBObject c){
		if(Action.save.equals(action) && c._id == null){ 
			Sample sample = (Sample)c;
			MongoDBDAO.deleteByCode(collectionName, c.getClass(), c.code);
			
			if(sample.projectCodes.size() == 1){
				String projectCode = sample.projectCodes.iterator().next(); 
				CodeHelper.getInstance().updateProjectSampleCodeWithLastSampleCode(projectCode);
			}else{
				contextValidation.addErrors("project","problem during rollback to update last sample code on projects "+sample.projectCodes.toString());
			}
		}else if(Action.update.equals(action)){
			//replace by old version of the object
		}		
	}
}
