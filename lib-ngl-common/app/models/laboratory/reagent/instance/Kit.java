package models.laboratory.reagent.instance;

import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.reagent.instance.KitValidationHelper;
import validation.utils.ValidationHelper;

public class Kit extends AbstractDeclaration{
	public String catalogCode;

	@Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, catalogCode, "catalogCode");
		ValidationHelper.required(contextValidation, code, "code");
		ValidationHelper.required(contextValidation, receptionDate, "receptionDate");
		
		if(!contextValidation.hasErrors()){
			KitValidationHelper.validateCode(this, InstanceConstants.REAGENT_INSTANCE_COLL_NAME, contextValidation);
			KitValidationHelper.validateKitCatalogCode(catalogCode, contextValidation);
		}
	}
	
}
