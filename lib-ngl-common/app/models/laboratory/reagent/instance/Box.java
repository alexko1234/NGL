package models.laboratory.reagent.instance;

import java.util.List;

import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.reagent.instance.BoxValidationHelper;
import validation.reagent.instance.KitValidationHelper;
import validation.utils.ValidationHelper;

import models.laboratory.common.instance.Comment;
import models.utils.InstanceConstants;


public class Box extends AbstractDeclaration{
	public String catalogCode;
	
	public String kitCode;

	public int stockNumber;
	public String stockPlace;
	
	public List<Comment> comments;

	@Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, code, "code");
		ValidationHelper.required(contextValidation, barCode, "barCode");
		ValidationHelper.required(contextValidation, receptionDate, "receptionDate");
		ValidationHelper.required(contextValidation, expirationDate, "expirationDate");
		ValidationHelper.required(contextValidation, state, "state");
		ValidationHelper.required(contextValidation, orderCode, "orderCode");
		
		if(!contextValidation.hasErrors()){
			CommonValidationHelper.validateUniqueInstanceCode(contextValidation, code, Box.class, InstanceConstants.REAGENT_INSTANCE_COLL_NAME);
			BoxValidationHelper.validateKitCode(kitCode, contextValidation);
			BoxValidationHelper.validateBoxCatalogCode(catalogCode, contextValidation);
		}
	}
}
