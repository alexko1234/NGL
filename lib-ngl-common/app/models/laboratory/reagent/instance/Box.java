package models.laboratory.reagent.instance;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.reagent.instance.BoxValidationHelper;
import validation.reagent.instance.KitValidationHelper;
import validation.utils.ValidationHelper;

import models.laboratory.common.description.State;
import models.laboratory.common.instance.Comment;
import models.utils.InstanceConstants;


public class Box extends AbstractDeclaration{
	public String catalogCode;
	
	public String kitCode;

	public String barCode;
	public String bundleBarCode;
	
	public Date startToUseDate;
	public Date stopToUseDate;
	
	public State state;
	
	public Date expirationDate;
	
	public String stockInformation;
	
	public List<Comment> comments;

	@Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, code, "code");
			ValidationHelper.required(contextValidation, catalogCode, "catalogCode");
			ValidationHelper.required(contextValidation, barCode, "barCode");
			ValidationHelper.required(contextValidation, receptionDate, "receptionDate");
			ValidationHelper.required(contextValidation, expirationDate, "expirationDate");
			ValidationHelper.required(contextValidation, state, "state");
			ValidationHelper.required(contextValidation, orderCode, "orderCode");
		
		if(!contextValidation.hasErrors()){
			BoxValidationHelper.validateCode(this, InstanceConstants.REAGENT_INSTANCE_COLL_NAME, contextValidation);
			if(StringUtils.isNotEmpty(kitCode)){
				BoxValidationHelper.validateKitCode(kitCode, contextValidation);
			}
			BoxValidationHelper.validateBoxCatalogCode(catalogCode, contextValidation);
		}
	}
}
