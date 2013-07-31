package models.laboratory.reagent.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.reagent.description.ReagentType;
import models.utils.HelperObjects;
import models.utils.IValidation;
import models.utils.InstanceConstants;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;
import validation.utils.BusinessValidationHelper;


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
	public void validate(Map<String, List<ValidationError>> errors) {
		BusinessValidationHelper.validateRequiredDescriptionCode(errors, this.reagentTypeCode, "reagentTypeCode", ReagentType.find);
		BusinessValidationHelper.validateRequiredInstanceCode(errors, this.reagentInstanceCode, "reagentInstanceCode", ReagentInstance.class,InstanceConstants.REAGENT_INSTANCE_COLL_NAME);
	}
	 
	
}
