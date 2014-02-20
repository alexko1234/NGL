package validation.container.instance;

import models.laboratory.container.description.ContainerSupportCategory;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;

public class SupportValidationHelper extends CommonValidationHelper {
	
	
	public static void validateSupportCategoryCode(
			String categoryCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ContainerSupportCategory.find,false);

	}

}
