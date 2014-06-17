package models.laboratory.common.instance;

import models.utils.InstanceConstants;
import validation.ContextValidation;

import validation.IValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationHelper;

public class Resolution implements IValidation {
	
	public String code;
    public String name;
    public Short displayOrder;
    public String level = "default";
    public ResolutionCategory category;
	
    
	@Override
	public void validate(ContextValidation contextValidation) {
		
    	contextValidation.putObject("resolutions", this);
    	
    	CommonValidationHelper.validateUniqueFieldValue(contextValidation, "code", this.code, ResolutionConfigurations.class, InstanceConstants.RESOLUTION_COLL_NAME );
    	
    	ValidationHelper.required(contextValidation, this.name, "name");
    	
    	ValidationHelper.required(contextValidation, this.category.name, "category.name");
    	
    	ValidationHelper.required(contextValidation, this.displayOrder, "displayOrder");
    	
    	ValidationHelper.required(contextValidation, this.category.displayOrder, "category.displayOrder");
    	
    	contextValidation.removeObject("resolutions");
    	
	}

}
