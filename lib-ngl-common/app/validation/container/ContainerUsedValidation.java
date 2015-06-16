package validation.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.Level.CODE;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.experiment.description.ExperimentType;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationHelper;

public class ContainerUsedValidation extends CommonValidationHelper{

	public static void validateExperimentProperties(String typeCode, Map<String,PropertyValue> properties, ContextValidation contextValidation,Boolean updateRequired) 
	{
		ExperimentType exType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ExperimentType.find,true);
		if(exType!=null){
			contextValidation.addKeyToRootKeyName("experimentproperties");
			
			//List<PropertyDefinition> propertyDefinitionsContainerIn=exType.getPropertyDefinitionByLevel(Level.CODE.ContainerIn);
			List<PropertyDefinition> propertyDefinitions=exType.getPropertyDefinitionByLevel(Level.CODE.valueOf(contextValidation.getObject("level").toString()));
			/*if(updateRequired){
				getPropertyDefintionNotRequired(propertyDefinitionsContainerIn);
				getPropertyDefintionNotRequired(propertyDefinitionsContainerOut);
			}*/
			//ValidationHelper.validateProperties(contextValidation, properties, propertyDefinitionsContainerIn, false);
			ValidationHelper.validateProperties(contextValidation, properties, propertyDefinitions, false);
			contextValidation.removeKeyFromRootKeyName("experimentproperties");
		}
	}
	
	
	public static void validateExperimentProperties(String typeCode, Map<String,PropertyValue> properties, ContextValidation contextValidation) 
	{		
		validateExperimentProperties( typeCode, properties, contextValidation,false);
	}
	
	
	public static  List<PropertyDefinition> getPropertyDefintionNotRequired(List<PropertyDefinition> propertyDefinitions){
		
		for(PropertyDefinition propertyDefinition:propertyDefinitions){
				propertyDefinition.required=false;
		}
		
		return propertyDefinitions;
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


}
