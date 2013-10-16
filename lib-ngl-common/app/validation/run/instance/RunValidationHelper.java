package validation.run.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.run.description.RunType;
import models.laboratory.run.instance.InstrumentUsed;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.Treatment;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;



public class RunValidationHelper extends CommonValidationHelper {
		
	public static void validateRunInstrumentUsed(InstrumentUsed instrumentUsed, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, instrumentUsed, "instrumentUsed")){
			contextValidation.addKeyToRootKeyName("instrumentUsed");
			instrumentUsed.validate(contextValidation); 
			contextValidation.removeKeyFromRootKeyName("instrumentUsed");
		}
	}

	public static void validateRunType(String typeCode,	Map<String, PropertyValue> properties,	ContextValidation contextValidation) {
		RunType runType = validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", RunType.find,true);
		if(null != runType){
			contextValidation.addKeyToRootKeyName("properties");
			ValidationHelper.validateProperties(contextValidation, properties, runType.getPropertyDefinitionByLevel(Level.CODE.Run), true);
			contextValidation.removeKeyFromRootKeyName("properties");
		}		
	}
	
	public static void validationContainerSupportCode(
			String containerSupportCode, ContextValidation contextValidation) {		
		if(ValidationHelper.required(contextValidation, containerSupportCode, "containerSupportCode")){
			//TODO add controles si le container existe dans mongo db
			//BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, this.containerSupportCode, "containerSupportCode", ContainerSupportCode.find, false);
		}		 
	}

}
