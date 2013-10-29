package models.laboratory.common.instance;

import java.util.Date;
import java.util.List;

import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

public class Validation implements IValidation {
	
	public TBoolean valid = TBoolean.UNSET;
    public Date date;
    public String user;
    public List<String> resolutionCodes;
    
	@Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, valid, "valid");
		if(!TBoolean.UNSET.equals(valid)){
			ValidationHelper.required(contextValidation, date, "date");
			ValidationHelper.required(contextValidation, user, "user");
			
		}
		
		//todo resolution si different de zero
	}

}
