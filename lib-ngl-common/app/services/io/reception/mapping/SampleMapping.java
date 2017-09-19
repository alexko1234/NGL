package services.io.reception.mapping;

import java.lang.reflect.Field;
import java.util.Map;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.reception.instance.AbstractFieldConfiguration;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import services.io.reception.Mapping;
import validation.ContextValidation;
import validation.utils.ValidationConstants;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public class SampleMapping extends Mapping<Sample> {
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
		//TODO update categoryCode if not a code but a label.
		if(sample.categoryCode == null){
			sample.categoryCode = SampleType.find.findByCode(sample.typeCode).category.code;
		}		
	}


	@Override
	public void consolidate(Sample sample) {
		// TODO Auto-generated method stub
		//update link between two sample need only from.sampleCode
		if(sample.life != null && sample.life.from != null && sample.life.from.sampleCode != null){
			Sample parentSample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.life.from.sampleCode);
			if(null != parentSample){
				sample.life.from.projectCodes =  parentSample.projectCodes;
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
				contextValidation.addErrors("sampleCode", ValidationConstants.ERROR_NOTEXISTS_MSG, sample.life.from.sampleCode);
			}
		}
	}	
	
	@Override
	public void synchronizeMongoDB(DBObject c){
		if(Action.save.equals(action)){
			Sample sample = (Sample)c;
			CodeHelper.getInstance().updateProjectSampleCodeIfNeeded(sample.projectCodes.iterator().next(), sample.code);
		}
		super.synchronizeMongoDB(c);
	}
}
