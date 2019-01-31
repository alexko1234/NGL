package models.laboratory.reagent.instance;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import models.laboratory.common.description.State;
import models.laboratory.common.instance.Comment;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.reagent.instance.BoxValidationHelper;
import validation.utils.ValidationHelper;


public class Box extends AbstractDeclaration {
	
	public String catalogCode;
	
	public String kitCode;

	public String providerID;
	public String lotNumber;
	
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
		ValidationHelper.required(contextValidation, providerID, "providerID");
		ValidationHelper.required(contextValidation, receptionDate, "receptionDate");
		ValidationHelper.required(contextValidation, expirationDate, "expirationDate");
		ValidationHelper.required(contextValidation, state, "state");
		ValidationHelper.required(contextValidation, orderCode, "orderCode");
		
		if(!contextValidation.hasErrors()){
			BoxValidationHelper.validateCode(this, InstanceConstants.REAGENT_INSTANCE_COLL_NAME, contextValidation);
			if(StringUtils.isNotEmpty(kitCode)){
				if(declarationType.equals("kit")){
					BoxValidationHelper.validateKitCode(kitCode, contextValidation);
				}
				BoxValidationHelper.validateBoxCatalogCode(catalogCode, contextValidation);
			}
		}
	}
}
