package models.laboratory.common.instance;

import java.util.Date;
import java.util.List;

import validation.ContextValidation;
import validation.IValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationHelper;

public class Valuation implements IValidation {
	
	public TBoolean valid = TBoolean.UNSET;
    public Date date;
    public String user;
    public List<String> resolutionCodes;
    
    public String valuationCode;
    
	@Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, valid, "valid");
		if(!TBoolean.UNSET.equals(valid)){
			ValidationHelper.required(contextValidation, date, "date");
			ValidationHelper.required(contextValidation, user, "user");
			
		}
		CommonValidationHelper.validateResolutionCodes(resolutionCodes, contextValidation);
		//TODO : resolution si different de zero
		
		CommonValidationHelper.validateValuationCode(valuationCode, contextValidation); 
		
	}

}
