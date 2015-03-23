package validation.reagent.instance;

import models.laboratory.reagent.description.KitCatalog;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationHelper;

public class KitValidationHelper extends CommonValidationHelper{
	public static void validateKitCatalogCode(String code, ContextValidation contextValidation){
		validateUniqueInstanceCode(contextValidation, code, KitCatalog.class, InstanceConstants.REAGENT_CATALOG_COLL_NAME);
	}
}
