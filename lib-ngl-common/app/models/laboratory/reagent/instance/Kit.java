package models.laboratory.reagent.instance;

import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.reagent.instance.KitValidationHelper;
import validation.utils.ValidationHelper;

public class Kit extends AbstractDeclaration{
	public String catalogCode;

	@Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, catalogCode, "catalogCode");
		ValidationHelper.required(contextValidation, code, "code");
		ValidationHelper.required(contextValidation, barCode, "barCode");
		ValidationHelper.required(contextValidation, receptionDate, "receptionDate");
		ValidationHelper.required(contextValidation, expirationDate, "expirationDate");
		ValidationHelper.required(contextValidation, state, "state");
		
		if(!contextValidation.hasErrors()){
			KitValidationHelper.validateUniqueInstanceCode(contextValidation, code, Kit.class, InstanceConstants.REAGENT_INSTANCE_COLL_NAME);
			KitValidationHelper.validateKitCatalogCode(catalogCode, contextValidation);
		}
	}
	
}
