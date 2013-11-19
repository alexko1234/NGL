package validation.run.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.run.description.ReadSetType;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;



public class ReadSetValidationHelper extends CommonValidationHelper {
	
	
	public static void validateReadSetCodeInRunLane(String readSetCode, String runCode, Integer laneNumber, ContextValidation contextValidation) {
		if(contextValidation.isUpdateMode() && !checkReadSetInRun(readSetCode, runCode, laneNumber)){
			contextValidation.addErrors("code",ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, readSetCode);
	}	
		
	}
	
	private static boolean checkReadSetInRun(String readSetCode, String runCode, Integer laneNumber) {
		return MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(
						DBQuery.is("code", runCode), 
						DBQuery.elemMatch("lanes", 
							DBQuery.and(
								DBQuery.is("number", laneNumber),
								DBQuery.is("readSetCodes", readSetCode)))));
	}
	
	public static void validateReadSetType(String typeCode,	Map<String, PropertyValue> properties, ContextValidation contextValidation) {
		ReadSetType readSetType = validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ReadSetType.find,true);
		if(null != readSetType){
			contextValidation.addKeyToRootKeyName("properties");
			ValidationHelper.validateProperties(contextValidation, properties, readSetType.getPropertyDefinitionByLevel(Level.CODE.ReadSet), true);
			contextValidation.removeKeyFromRootKeyName("properties");
		}		
	}


	public static void validateReadSetRunCode(String runCode,	ContextValidation contextValidation) {
		validateRequiredInstanceCode(runCode, "runCode",  Run.class, InstanceConstants.RUN_ILLUMINA_COLL_NAME,contextValidation);		
	}


	public static void validateReadSetLaneNumber(String runCode, Integer laneNumber, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, runCode, "runCode") && 
				ValidationHelper.required(contextValidation, laneNumber, "laneNumber")){
			if(!isLaneExist(runCode, laneNumber, contextValidation)){
				contextValidation.addErrors("runCode",ValidationConstants.ERROR_NOTEXISTS_MSG, runCode);
				contextValidation.addErrors("laneNumber",ValidationConstants.ERROR_NOTEXISTS_MSG, laneNumber);
			}
		}		
	}
	
	private static boolean isLaneExist(String runCode, Integer laneNumber, ContextValidation contextValidation) {		
		return MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.is("lanes.number", laneNumber)));
	}
	
		
		
	public static void validateSampleAndProjectCode(String projectCode, String sampleCode, ContextValidation contextValidation) {
		CommonValidationHelper.validateProjectCode(projectCode, contextValidation); // verify required property
		
		if(ValidationHelper.required(contextValidation, sampleCode, "sampleCode")) {
			CommonValidationHelper.validateSampleCode(sampleCode, contextValidation); // verify exist code

			//verify projectCode in relation to sampleCode
			 List<Sample> samps =  MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("code", sampleCode)).toList();
			 Boolean bFind = false;
			 for (Sample sample : samps) {
				 if ((sample != null) && (sample.projectCodes != null) && (sample.projectCodes.contains(projectCode))) {
					 bFind = true;
				 }
			 }
			 if (!bFind) {
				 contextValidation.addErrors("projectCode", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, projectCode);
			 }

		}
	}


}
