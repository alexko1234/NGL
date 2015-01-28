package models.laboratory.reagent.description;

import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;

public class KitCatalog extends AbstractCatalog{

	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
		CommonValidationHelper.validateUniqueInstanceCode(contextValidation, code, getClass(), InstanceConstants.REAGENT_CATALOG_COLL_NAME);
	}

}
