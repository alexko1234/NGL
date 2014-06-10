package models.laboratory.common.instance;

import validation.ContextValidation;

import validation.ContextValidation;
import validation.IValidation;

public class StateResolution implements IValidation {
	
	public String  code;
    public String name;
    public String categoryCode;
    public Short displayOrder;
    public String level = "default";
	
    
	@Override
	public void validate(ContextValidation contextValidation) {
	}

}
