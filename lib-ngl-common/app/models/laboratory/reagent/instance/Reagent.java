package models.laboratory.reagent.instance;

import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.reagent.instance.ReagentValidationHelper;
import validation.utils.ValidationHelper;

public class Reagent extends AbstractDeclaration {
	
	public String catalogCode;
	public String boxCode;
	public String kitCode;
	
	public int stockNumber;

	@Override
	public void validate(ContextValidation contextValidation) {
				ValidationHelper.required(contextValidation, code, "code");
				ValidationHelper.required(contextValidation, barCode, "barCode");
				ValidationHelper.required(contextValidation, receptionDate, "receptionDate");
				ValidationHelper.required(contextValidation, expirationDate, "expirationDate");
				ValidationHelper.required(contextValidation, state, "state");
				ValidationHelper.required(contextValidation, orderCode, "orderCode");
				
				if(!contextValidation.hasErrors()){
					CommonValidationHelper.validateUniqueInstanceCode(contextValidation, code, Reagent.class, InstanceConstants.REAGENT_INSTANCE_COLL_NAME);
					ReagentValidationHelper.validateBoxCode(boxCode, contextValidation);
					ReagentValidationHelper.validateReagentCatalogCode(catalogCode, contextValidation);
				}
	}
	
}
