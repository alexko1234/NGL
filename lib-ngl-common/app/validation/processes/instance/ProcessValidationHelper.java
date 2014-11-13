package validation.processes.instance;

import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
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
			ValidationHelper.validateProperties(contextValidation, properties, processType.getPropertiesDefinitionDefaultLevel());
		}
		
	}

	public static void validateProcessCategory(String categoryCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ProcessCategory.find,false);
		
	}

	public static void validateCurrentExperimentTypeCode(String currentExperimentTypeCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, currentExperimentTypeCode, "currentExperimentTypeCode", ExperimentType.find,false);
	}

}
