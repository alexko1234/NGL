package validation.run.instance;

import java.util.Map;

import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.run.description.ReadSetType;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.SampleOnContainer;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;

public class ReadSetValidationHelper extends CommonValidationHelper {
		
	public static void validateReadSetCodeInRunLane(String readSetCode, String runCode, Integer laneNumber, ContextValidation contextValidation) {
		if (contextValidation.isUpdateMode() && !checkReadSetInRun(readSetCode, runCode, laneNumber)) {
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
								DBQuery.in("readSetCodes", readSetCode)))));
	}
	
	public static void validateReadSetType(String typeCode,	Map<String, PropertyValue> properties, ContextValidation contextValidation) {
		ReadSetType readSetType = validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ReadSetType.find,true);
		if (readSetType != null) {
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
			if (!isLaneExist(runCode, laneNumber, contextValidation)) {
				contextValidation.addErrors("runCode",ValidationConstants.ERROR_NOTEXISTS_MSG, runCode);
				contextValidation.addErrors("laneNumber",ValidationConstants.ERROR_NOTEXISTS_MSG, laneNumber);
			}
		}		
	}
	
	private static boolean isLaneExist(String runCode, Integer laneNumber, ContextValidation contextValidation) {		
		return MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.is("lanes.number", laneNumber)));
	}
	
	public static void validateSampleOnContainer(SampleOnContainer sampleOnContainer, ContextValidation contextValidation) {
		if (ValidationHelper.required(contextValidation, sampleOnContainer, "sampleOnContainer")) {
			sampleOnContainer.validate(contextValidation);
		}
	}

}
