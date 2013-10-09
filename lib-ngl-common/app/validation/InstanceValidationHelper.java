package validation;

import validation.utils.ValidationConstants;
import  validation.utils.ValidationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import fr.cea.ig.DBObject;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.project.instance.Project;
import models.laboratory.reagent.instance.ReagentInstance;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.laboratory.sample.instance.Sample;
import models.laboratory.stock.instance.Stock;
import models.utils.InstanceConstants;

import validation.utils.BusinessValidationHelper;


public class InstanceValidationHelper {
	
	public static void validationProjectCodes(List<String> projectCodes,ContextValidation contextValidation){
		BusinessValidationHelper.validateRequiredInstanceCodes(contextValidation, projectCodes, "projectCodes",Project.class,InstanceConstants.PROJECT_COLL_NAME,false);
	}

	public static void validationSampleCodes(List<String> sampleCodes,ContextValidation contextValidation){
		BusinessValidationHelper.validateRequiredInstanceCodes(contextValidation, sampleCodes,"sampleCodes",Sample.class,InstanceConstants.SAMPLE_COLL_NAME,false);
	}

	public static void validationExperimentCodes(List<String> experimentCodes,ContextValidation contextValidation){
		BusinessValidationHelper.validateExistInstanceCodes(contextValidation, experimentCodes, "fromExperimentTypeCodes", Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME, false);
	}

	public static void validationExperimentCode(String experimentCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistInstanceCode(contextValidation, experimentCode, "fromPurifingCode", Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME, false);
	}

	public static void validationSampleCode(String sampleCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistInstanceCode(contextValidation, sampleCode, "sampleCode", Sample.class, InstanceConstants.SAMPLE_COLL_NAME, false);

	}

	public static void validationStockCode(String stockCode,ContextValidation contextValidation){
		BusinessValidationHelper.validateExistInstanceCode(contextValidation, stockCode, "stockCode",Stock.class,InstanceConstants.STOCK_COLL_NAME ,false);

	}

	public static void validationContainerCode(String containerCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, containerCode, "containerCode", Container.class,InstanceConstants.CONTAINER_COLL_NAME);
	}

	public static void validationProjectCode(String projectCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, projectCode,"projectCode",Project.class,InstanceConstants.PROJECT_COLL_NAME,false);
	}

	public static void validationContents(List<Content> contents,
			ContextValidation contextValidation) {
		String rootKeyName=null;
		if(ValidationHelper.required(contextValidation, contents, "contents")){
			
			for(int i=0; i<contents.size();i++){
				rootKeyName="content"+"["+i+"]";
				contextValidation.addKeyToRootKeyName(rootKeyName);
				contents.get(i).validate(contextValidation);
				contextValidation.removeKeyFromRootKeyName(rootKeyName);
			}
		}
		
	}

	public static void validationComments(List<Comment> comments,
			ContextValidation contextValidation) {
		if(comments != null){
			for(Comment comment:comments){
				comment.validate(contextValidation);
			}
		}
	}

	public static void validationContainerSupport(ContainerSupport support,
			ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("containersupport");
		if(ValidationHelper.required(contextValidation, support, "containersupport")) {
			support.validate(contextValidation);
		}
		contextValidation.removeKeyFromRootKeyName("containersupport");
	}

	public static void validationReagentInstanceCode(String reagentInstanceCode, ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("reagent");
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, reagentInstanceCode, "reagentInstanceCode", ReagentInstance.class,InstanceConstants.REAGENT_INSTANCE_COLL_NAME);
		contextValidation.removeKeyFromRootKeyName("reagent");
	}

	public static void validationLanes(List<Lane> lanes, ContextValidation contextValidation) {
		//TODO number of lanes (depends of the type run and the mode incremental insert or full insert !!!)
		//TODO validate lane number
		if(null != lanes && lanes.size() > 0) {
			int index = 0;
			Set<Integer> laneNumbers = new TreeSet<Integer>();
			for (Lane lane : lanes) {
				if (lane != null) {
					contextValidation.addKeyToRootKeyName("lanes[" + index + "]");
					lane.validate(contextValidation);
					if(laneNumbers.contains(lane.number)){
						contextValidation.addErrors("number", ValidationConstants.ERROR_NOTUNIQUE_MSG, lane.number);
					}
					laneNumbers.add(lane.number);
					contextValidation.removeKeyFromRootKeyName("lanes[" + index + "]");
				}
				index++;
			}
		}
	}


	public static void validationFiles(List<File> files, ContextValidation contextValidation) {
		if((null != files) && (files.size() > 0)) {
			int index = 0;
			List<String> lstFullName = new ArrayList<String>();
			for (File file : files) {
				contextValidation.addKeyToRootKeyName("files[" + index + "]");
				if (!lstFullName.contains(file.fullname)) {
					file.validate(contextValidation);
					lstFullName.add(file.fullname);
				}
				else { contextValidation.addErrors("fullname", ValidationConstants.ERROR_NOTUNIQUE_MSG, file.fullname); }
				contextValidation.removeKeyFromRootKeyName("files[" + index + "]");
				index++;
			}
		}
	}

	public static void validationTreatments(Map<String, Treatment> treatments, ContextValidation contextValidation) {
		if(null != treatments){
			List<String> trNames = new ArrayList<String>();
			contextValidation.addKeyToRootKeyName("treatments");
			for(Treatment t:treatments.values()){
				contextValidation.addKeyToRootKeyName(t.code);
				if(!trNames.contains(t.code) && treatments.containsKey(t.code)){										
					trNames.add(t.code);
					t.validate(contextValidation);					
				}else if(trNames.contains(t.code)){
					contextValidation.addErrors("code", ValidationConstants.ERROR_NOTUNIQUE_MSG, t.code);
				} else{
					contextValidation.addErrors("code", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, t.code);
				}
				contextValidation.removeKeyFromRootKeyName(t.code);
			}
			contextValidation.removeKeyFromRootKeyName("treatments");
		}
		
	}
	/**
	 * Validate the id of dbObject
	 * @param dbObject
	 * @param contextValidation
	 */
	public static void validateId(DBObject dbObject, ContextValidation contextValidation) {
		if(contextValidation.isUpdateMode()){
    		ValidationHelper.required(contextValidation, dbObject._id, "_id");
    	}else if(contextValidation.isCreationMode() && null != dbObject._id){
    		contextValidation.addErrors("_id", ValidationConstants.ERROR_ID_NOTNULL_MSG);
    	}
	}
	/**
	 * Validate the code of an dbObject. the code is the NGL identifier
	 * @param dbObject
	 * @param collectionName
	 * @param contextValidation
	 */
	public static void validateCode(DBObject dbObject, String collectionName, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, dbObject.code, "code")){
		    if (contextValidation.isCreationMode()) {
				BusinessValidationHelper.validateUniqueInstanceCode(contextValidation, dbObject.code, dbObject.getClass(), collectionName);		
			}else if(contextValidation.isUpdateMode()){
				BusinessValidationHelper.validateExistInstanceCode(contextValidation, dbObject.code, dbObject.getClass(), collectionName);
			}
		}
		
	}

	public static void validateTraceInformation(TraceInformation traceInformation, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, traceInformation, "traceInformation")){
			contextValidation.addKeyToRootKeyName("traceInformation");
			traceInformation.validate(contextValidation);
			contextValidation.removeKeyFromRootKeyName("traceInformation");
		}		
	}

}