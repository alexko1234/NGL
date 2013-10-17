package models.laboratory.common.instance;

import java.util.Date;
import java.util.List;

import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

public class State implements IValidation {
	
	public String stateCode;
	public Date date;
    public String user;
    public List<String> resolutionCode;
    
	@Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, stateCode, "stateCode");
		ValidationHelper.required(contextValidation, date, "date");
		ValidationHelper.required(contextValidation, user, "user");		
	}

}
