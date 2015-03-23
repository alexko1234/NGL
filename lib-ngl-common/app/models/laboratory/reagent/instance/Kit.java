package models.laboratory.reagent.instance;

import validation.ContextValidation;
import validation.reagent.instance.KitValidationHelper;
import validation.utils.ValidationHelper;

public class Kit extends AbstractDeclaration{
	public String catalogCode;

	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
		ValidationHelper.required(contextValidation, catalogCode, "catalogCode");
		if(!contextValidation.hasErrors()){
			KitValidationHelper.validateKitCatalogCode(catalogCode, contextValidation);
		}
	}
	
}
