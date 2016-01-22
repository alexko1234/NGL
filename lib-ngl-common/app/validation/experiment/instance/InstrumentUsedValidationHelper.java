package validation.experiment.instance;

import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;

public class InstrumentUsedValidationHelper extends CommonValidationHelper {

	public static void validationTypeCode(
			String instrumentUsedTypeCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, instrumentUsedTypeCode, "typeCode", InstrumentUsedType.find);		
	}

	public static void validationCode(String code,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, code, "code", Instrument.find);
		
	}

	public static void validationCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", InstrumentCategory.find);
	}

	public static void validationInContainerSupportCategoryCode(String inContainerSupportCategoryCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, inContainerSupportCategoryCode, "inContainerSupportCategoryCode", ContainerSupportCategory.find);				
	}

	public static void validationOutContainerSupportCategoryCode(String outContainerSupportCategoryCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, outContainerSupportCategoryCode, "outContainerSupportCategoryCode", ContainerSupportCategory.find);
	}
	
	
}
