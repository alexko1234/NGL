package models.laboratory.reagent.instance;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import models.laboratory.common.description.State;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.reagent.instance.KitValidationHelper;
import validation.reagent.instance.ReagentValidationHelper;
import validation.utils.ValidationHelper;

public class Reagent extends AbstractDeclaration {
	
	public String boxCode;//To delete
	
	public String catalogCode;
	public String boxBarCode;
	public String kitCode;
	
	public Date startToUseDate;
	public Date stopToUseDate;
	
	public State state;
	
	public Date expirationDate;
	
	public String barCode;
	public String bundleBarCode;
	
	public String boxCatalogRefCode;
	
	public String stockInformation;

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
					ReagentValidationHelper.validateCode(this, InstanceConstants.REAGENT_INSTANCE_COLL_NAME, contextValidation);
					ReagentValidationHelper.validateReagentCatalogCode(catalogCode, contextValidation);
				}
	}
	
}
