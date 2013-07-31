package models.laboratory.reagent.instance;


import models.laboratory.reagent.description.ReagentType;
import models.utils.HelperObjects;
import models.utils.IValidation;
import models.utils.InstanceConstants;

import org.codehaus.jackson.annotate.JsonIgnore;


import validation.utils.BusinessValidationHelper;
import validation.utils.ContextValidation;


public class ReagentUsed implements IValidation {
	
	// Reagent ref
	public String reagentTypeCode;
	public String reagentInstanceCode;
	// to complete with Map properties or attribut or Values class
	//public Map<String,PropertyValue> properties;
	
	@JsonIgnore
	public ReagentType getReagentType() {
		return new HelperObjects<ReagentType>().getObject(ReagentType.class, reagentTypeCode);
	}

	@Override
	public void validate(ContextValidation contextErrors) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextErrors.errors, this.reagentTypeCode, "reagentTypeCode", ReagentType.find);
		BusinessValidationHelper.validateRequiredInstanceCode(contextErrors.errors, this.reagentInstanceCode, "reagentInstanceCode", ReagentInstance.class,InstanceConstants.REAGENT_INSTANCE_COLL_NAME);
	}
	 
	
}
