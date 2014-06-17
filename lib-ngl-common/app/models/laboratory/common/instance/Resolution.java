package models.laboratory.common.instance;

import models.utils.InstanceConstants;
import validation.ContextValidation;

import validation.ContextValidation;
import validation.IValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationHelper;

public class Resolution implements IValidation {
	
	public String code;
    public String name;
    public String categoryCode;
    public Short displayOrder;
    public String level = "default";
    
	public Resolution() {
		super();
	}
	
    
	@Override
	public void validate(ContextValidation contextValidation) {
		
    	contextValidation.putObject("resolutions", this);
    	
    	CommonValidationHelper.validateUniqueFieldValue(contextValidation, "code", this.code, ResolutionConfigurations.class, InstanceConstants.RESOLUTION_COLL_NAME );
    	
    	ValidationHelper.required(contextValidation, this.name, "name");
    	
    	CommonValidationHelper.validateCategoryCode(categoryCode, contextValidation);
    	
    	ValidationHelper.required(contextValidation, this.displayOrder, "displayOrder");
    	
    	contextValidation.removeObject("resolutions");
    	
	}

}
