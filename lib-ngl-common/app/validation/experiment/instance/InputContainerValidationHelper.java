package validation.experiment.instance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.InputContainerUsed;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationHelper;

public class InputContainerValidationHelper extends CommonValidationHelper {

	public static void compareInputContainerWithContainer(
			ContextValidation contextValidation, InputContainerUsed inputContainer,
			Container container) {
		
		if(!inputContainer.categoryCode.equals(container.categoryCode)){
			contextValidation.addErrors("categoryCode", "error.validationexp.inputContainer.categoryCode.notequals", inputContainer.code);
		}
		
		if(inputContainer.contents.size() != container.contents.size()){
			contextValidation.addErrors("categoryCode", "error.validationexp.inputContainer.contents.sizenotequals", inputContainer.code);
		}		
		//TODO improve comparison
	}
	
	public static void validateExperimentProperties(ContextValidation contextValidation, Map<String,PropertyValue> properties, Level.CODE level) {
		String typeCode = getObjectFromContext(FIELD_TYPE_CODE, String.class, contextValidation);
		
		ExperimentType exType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ExperimentType.find,true);
		if(exType!=null){
			contextValidation.addKeyToRootKeyName("experimentProperties");
			List<PropertyDefinition> propertyDefinitions=exType.getPropertyDefinitionByLevel(level);
			ValidationHelper.validateProperties(contextValidation, properties, propertyDefinitions, false);
			contextValidation.removeKeyFromRootKeyName("experimentProperties");
		}
	}
	
	public static void validateInstrumentProperties(ContextValidation contextValidation, Map<String,PropertyValue> properties,  Level.CODE level) {
		String typeCode = getObjectFromContext(FIELD_TYPE_CODE, String.class, contextValidation);
		
		ExperimentType exType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ExperimentType.find,true);
		if(exType!=null){
			contextValidation.addKeyToRootKeyName("instrumentProperties");
			List<PropertyDefinition> propertyDefinitions=exType.getPropertyDefinitionByLevel(level);
			ValidationHelper.validateProperties(contextValidation, properties, propertyDefinitions, false);
			contextValidation.removeKeyFromRootKeyName("instrumentProperties");
		}
	}

	public static void validatePercentage(ContextValidation contextValidation, Double percentage) {
		if(ValidationHelper.required(contextValidation, percentage, "percentage")){
			if(percentage.doubleValue() > 100 && percentage.doubleValue() <= 0){
				contextValidation.addErrors("percentage", "error.validationexp.inputContainer.percentage");
			}
		}
		
	}

	public static void validateVolume(ContextValidation contextValidation, PropertyValue volume) {
		if(volume!=null && volume.value!=null){
			Collection<PropertyDefinition> pdefs = new ArrayList<>();		
			PropertyDefinition pd = new PropertyDefinition();			
			pd.code = "volume";
			pd.valueType = Double.class.getName();
			pdefs.add(pd);
			contextValidation.putObject("propertyDefinitions", pdefs);
			volume.validate(contextValidation);
			contextValidation.removeObject("propertyDefinitions");
		}
	}


	public static void validateConcentration(ContextValidation contextValidation, PropertyValue concentration) {
		if(concentration!=null && concentration.value!=null){
			Collection<PropertyDefinition> pdefs = new ArrayList<>();
			PropertyDefinition pd = new PropertyDefinition();
			pd.code = "concentration";
			pd.valueType = Double.class.getName();
			pdefs.add(pd);
			contextValidation.putObject("propertyDefinitions", pdefs);
			concentration.validate(contextValidation);
			contextValidation.removeObject("propertyDefinitions");
		}
		
	}
	
	public static void validateQuantity(ContextValidation contextValidation, PropertyValue quantity) {
		if(quantity != null && quantity.value != null){
			Collection<PropertyDefinition> pdefs = new ArrayList<>();
			PropertyDefinition pd = new PropertyDefinition();
			pd.code = "quantity";
			pd.valueType = Double.class.getName();
			pdefs.add(pd);
			contextValidation.putObject("propertyDefinitions", pdefs);
			quantity.validate(contextValidation);
			contextValidation.removeObject("propertyDefinitions");
		}
		
	}
	
}
