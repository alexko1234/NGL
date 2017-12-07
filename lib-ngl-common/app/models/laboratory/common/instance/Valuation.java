package models.laboratory.common.instance;

import java.util.Date;
import java.util.Set;

import validation.ContextValidation;
import validation.IValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationHelper;

public class Valuation implements IValidation {
	
	/**
	 * Validity.
	 */
	public TBoolean valid = TBoolean.UNSET;
	
	/**
	 * Creation date.
	 */
    public Date date;
    
    /**
     * Creation user. 
     */
    public String user;
    
    /**
     * Extra info.
     */
    public Set<String> resolutionCodes;
    
    /**
     * Name of the rules to validate containing instance.
     */
    public String criteriaCode;
    
    /**
     * Comment.
     */
    public String comment;
    
	@Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, valid, "valid");
		if(!TBoolean.UNSET.equals(valid)){
			ValidationHelper.required(contextValidation, date, "date");
			ValidationHelper.required(contextValidation, user, "user");
			
		}
		CommonValidationHelper.validateResolutionCodes(resolutionCodes, contextValidation);
		//TODO : resolution si different de zero
		
		CommonValidationHelper.validateCriteriaCode(criteriaCode, contextValidation); 
		
	}

}
