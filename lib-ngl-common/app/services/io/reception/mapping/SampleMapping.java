package services.io.reception.mapping;

//import static fr.cea.ig.play.IGGlobals.akkaSystem;

import java.lang.reflect.Field;
import java.util.Map;

import javax.inject.Inject;

import org.mongojack.DBQuery;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.migration.NGLContext;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.reception.instance.AbstractFieldConfiguration;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.instance.SampleHelper;
import services.io.reception.Mapping;
import validation.ContextValidation;
import validation.utils.ValidationConstants;

public class SampleMapping extends Mapping<Sample> {
	
	private static final play.Logger.ALogger logger = play.Logger.of(SampleMapping.class);
	
	// private static ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor6.class));
	// private /*static*/ ActorRef rulesActor;// = akkaSystem().actorOf(Props.create(RulesActor6.class));
//	private final LazyRules6Actor rulesActor;
	
	/*
	 * 
	 * @param objects : list of all db objects need by type samples, supports, containers
	 * @param configuration : the filed configuration for the current type
	 * @param action
	 * @param contextValidation
	 */
	@Inject
	public SampleMapping(Map<String, Map<String, DBObject>> objects, 
						 Map<String, ? extends AbstractFieldConfiguration> configuration, 
						 Action action, 
						 ContextValidation contextValidation,
						 NGLContext ctx) {
		super(objects, configuration, action, InstanceConstants.SAMPLE_COLL_NAME, Sample.class, Mapping.Keys.sample, contextValidation);
		// rulesActor = ctx.akkaSystem().actorOf(Props.create(RulesActor6.class));
//		rulesActor = ctx.rules6Actor();
	}
	
	/*
	 * convert a file line in Sample
	 * we override the defaut comportment to reused a prexist sample.
	 * @param rowMap
	 * @return
	 */
	@Override
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
	
	@Override
	protected void update(Sample sample) {
		
		if (Action.update.equals(action)) {
			sample.traceInformation.setTraceInformation(contextValidation.getUser());
		} else {
			sample.traceInformation = new TraceInformation(contextValidation.getUser());
		}
		//update categoryCode by default.
		//FDS 28/02/2018 catch the case when sample.typeCode is not valid
		//FDS 29/03/2018 NGL-1969: remplacer  findByCode  par findByCodeOrName
		if (null==SampleType.find.findByCodeOrName(sample.typeCode) ) {
			contextValidation.addErrors("sample.typeCode", ValidationConstants.ERROR_NOTEXISTS_MSG, sample.typeCode);
		} else {
			sample.categoryCode = SampleType.find.findByCodeOrName(sample.typeCode).category.code;
			// il faut ecraser  sample.typeCode d'entree (qui peut etre un Name!) par le code ramené de la base
			sample.typeCode= SampleType.find.findByCodeOrName(sample.typeCode).code;
			
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
		}
		
		//Call rules to add properties to sample
		logger.debug("sample "+sample);
		SampleHelper.executeRules(sample, "sampleCreation");
		logger.debug("sample "+sample);
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
