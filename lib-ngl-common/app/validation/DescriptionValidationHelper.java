package validation;

import java.util.List;
import java.util.Map;

import models.laboratory.common.description.State;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.reagent.description.ReagentType;
import models.laboratory.resolutions.description.Resolution;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationHelper;

public class DescriptionValidationHelper {

	public static void validationInstrumentUsedTypeCode(
			String instrumentUsedTypeCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, instrumentUsedTypeCode, "typeCode", InstrumentUsedType.find);		
	}
	
	
	public static void validationExperimentType(
			String typeCode, Map<String,PropertyValue> properties, ContextValidation contextValidation) {
		ExperimentType exType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ExperimentType.find,true);
		if(exType!=null){
			ValidationHelper.validateProperties(contextValidation, properties, exType.getPropertiesDefinitionDefaultLevel(), true);
		}
	}
	
	

	public static void validationInstrumentCode(String code,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, code, "instrumentUsed.code", Instrument.find);
		
	}

	public static void validationInstrumentCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "instrumentUsed.categoryCode", InstrumentCategory.find);
	}

	public static void validationReagentTypeCode(String reagentTypeCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, reagentTypeCode, "typeCode", ReagentType.find);		
	}

	public static void validationRunTypeCode(String typeCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ExperimentType.find,false);		
		
	}

	
	
	/**
	 * 
	 * @param stateCode
	 * @param contextValidation
	 * @deprecated "used InstanceValidationHelper.validationStateCode"
	 */
	public static void validationStateCode(String stateCode,ContextValidation contextValidation){
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, stateCode,"stateCode", State.find);
	}

	public static void validationResolutionCode(String resolutionCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistDescriptionCode(contextValidation, resolutionCode,"resolutionCode", Resolution.find);		
	}
	
	public static void validationResolutionCodes(List<String> resolutionCodes,
			ContextValidation contextValidation) {
		for(String resolutionCode:resolutionCodes){
			BusinessValidationHelper.validateExistDescriptionCode(contextValidation, resolutionCode,"resolutionCode", Resolution.find);
		}
	}

	
	
}
