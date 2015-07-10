package validation.reagent.instance;

import models.laboratory.reagent.instance.Box;
import models.laboratory.reagent.instance.Kit;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;

public class BoxValidationHelper extends CommonValidationHelper{
	public static void validateBoxCatalogCode(String code, ContextValidation contextValidation){
		validateExistInstanceCode(contextValidation, code, Box.class, InstanceConstants.REAGENT_CATALOG_COLL_NAME);
	}
	
	public static void validateKitCode(String code, ContextValidation contextValidation){
		validateExistInstanceCode(contextValidation, code, Kit.class, InstanceConstants.REAGENT_INSTANCE_COLL_NAME);
	}
}
