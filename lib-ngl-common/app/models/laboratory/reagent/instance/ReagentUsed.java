package models.laboratory.reagent.instance;

import models.laboratory.reagent.description.ReagentType;
import models.utils.HelperObjects;

import org.codehaus.jackson.annotate.JsonIgnore;


public class ReagentUsed {
	
	// Reagent ref
	public String reagentCode;
	public String reagentInstanceCode;
	// to complete with Map properties or attribut or Values class
	//public Map<String,PropertyValue> properties;
	
	@JsonIgnore
	public ReagentType getReagentType() {
		return new HelperObjects<ReagentType>().getObject(ReagentType.class, reagentCode);
	}
	 
	
}
