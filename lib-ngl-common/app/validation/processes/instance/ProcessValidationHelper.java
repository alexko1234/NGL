package validation.processes.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.SampleOnInputContainer;

import org.apache.commons.collections.CollectionUtils;

import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationHelper;

public class ProcessValidationHelper extends CommonValidationHelper {

	public static void validateProcessType(String typeCode,
			Map<String, PropertyValue> properties,
			ContextValidation contextValidation) {
		ProcessType processType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ProcessType.find,true);
		if(processType!=null){
			contextValidation.addKeyToRootKeyName("properties");
			ValidationHelper.validateProperties(contextValidation, properties, processType.getPropertiesDefinitionDefaultLevel());
			contextValidation.removeKeyFromRootKeyName("properties");
		}
		
	}

	public static void validateProcessCategory(String categoryCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ProcessCategory.find,false);
		
	}

	public static void validateCurrentExperimentTypeCode(String currentExperimentTypeCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistDescriptionCode(contextValidation, currentExperimentTypeCode, "currentExperimentTypeCode", ExperimentType.find,false);
	}

	public static void validateExperimentCodes(List<String> experimentCodes, ContextValidation contextValidation) {

		if(CollectionUtils.isNotEmpty(experimentCodes)){
			for(String expCode:experimentCodes){
				CommonValidationHelper.validateExperimenCode(expCode, contextValidation);
			}
		}
	}
	
	public static void validateStateCode(String stateCode,ContextValidation contextValidation){
		contextValidation.addKeyToRootKeyName("state");
		CommonValidationHelper.validateStateCode(stateCode,ObjectType.CODE.Process, contextValidation);
		contextValidation.removeKeyFromRootKeyName("state");
	}
	
	public static void validateSampleOnInputContainer(SampleOnInputContainer soic, ContextValidation contextValidation ){				
		if(ValidationHelper.required(contextValidation, soic, "sampleOnInputContainer")){		
			contextValidation.addKeyToRootKeyName("sampleOnInputContainer");
			soic.validate(contextValidation);
			contextValidation.removeKeyFromRootKeyName("sampleOnInputContainer");
		}
	}

}
