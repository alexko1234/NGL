package validation.container;

import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.experiment.description.ExperimentType;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationHelper;

public class ContainerUsedValidation extends CommonValidationHelper{

	public static void validateExperimentProperties(String typeCode, Map<String,PropertyValue> properties, ContextValidation contextValidation) 
	{
		ExperimentType exType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ExperimentType.find,true);
		if(exType!=null){
			contextValidation.addKeyToRootKeyName("experimentproperties");
			ValidationHelper.validateProperties(contextValidation, properties, exType.getPropertyDefinitionByLevel(Level.CODE.ContainerIn), false);
			ValidationHelper.validateProperties(contextValidation, properties, exType.getPropertyDefinitionByLevel(Level.CODE.ContainerOut), false);
			contextValidation.removeKeyFromRootKeyName("experimentproperties");
		}
	}


}
