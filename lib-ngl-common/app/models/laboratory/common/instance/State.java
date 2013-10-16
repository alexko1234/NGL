package models.laboratory.common.instance;

import java.util.Date;

import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

public class State implements IValidation {
	
	public String stateCode;
	public Date date;
    public String user;
    
	@Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, stateCode, "stateCode");
		ValidationHelper.required(contextValidation, date, "date");
		ValidationHelper.required(contextValidation, user, "user");		
	}

}
