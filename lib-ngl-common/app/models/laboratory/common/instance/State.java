package models.laboratory.common.instance;

import java.util.Date;
import java.util.List;

import validation.ContextValidation;
import validation.IValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationHelper;

public class State implements IValidation {

	public String code;
	public Date date = new Date();
	public String user;
	public List<String> resolutionCodes;

	public List<TransientState> historical;

	@Override
	public void validate(ContextValidation contextValidation) {
		CommonValidationHelper.validateStateCode(this.code, contextValidation);
		ValidationHelper.required(contextValidation, date, "date");
		ValidationHelper.required(contextValidation, user, "user");
		CommonValidationHelper.validateResolutionCodes(resolutionCodes,
				contextValidation);
	}

}
