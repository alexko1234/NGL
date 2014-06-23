package models.laboratory.reagent.instance;


import models.laboratory.reagent.description.ReagentType;
import models.utils.HelperObjects;
import models.utils.InstanceConstants;

import com.fasterxml.jackson.annotation.JsonIgnore;


import validation.ContextValidation;
import validation.DescriptionValidationHelper;
import validation.IValidation;
import validation.InstanceValidationHelper;
import validation.utils.BusinessValidationHelper;


public class ReagentUsed implements IValidation {
	
	// Reagent ref
	public String typeCode;
	public String code;
	// to complete with Map properties or attribut or Values class
	//public Map<String,PropertyValue> properties;
	
	@JsonIgnore
	public ReagentType getReagentType() {
		return new HelperObjects<ReagentType>().getObject(ReagentType.class, typeCode);
	}

	@Override
	public void validate(ContextValidation contextValidation) {
		
		DescriptionValidationHelper.validationReagentTypeCode(typeCode,contextValidation);
		InstanceValidationHelper.validationReagentInstanceCode(code,contextValidation);

	}
	 
	
}
