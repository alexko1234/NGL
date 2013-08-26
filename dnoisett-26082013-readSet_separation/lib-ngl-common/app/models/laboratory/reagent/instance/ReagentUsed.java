package models.laboratory.reagent.instance;


import models.laboratory.reagent.description.ReagentType;
import models.utils.HelperObjects;
import models.utils.IValidation;
import models.utils.InstanceConstants;

import org.codehaus.jackson.annotate.JsonIgnore;


import validation.DescriptionValidationHelper;
import validation.InstanceValidationHelper;
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
	public void validate(ContextValidation contextValidation) {
		
		DescriptionValidationHelper.validationReagentTypeCode(reagentTypeCode,contextValidation);
		InstanceValidationHelper.validationReagentInstanceCode(reagentInstanceCode,contextValidation);

	}
	 
	
}
