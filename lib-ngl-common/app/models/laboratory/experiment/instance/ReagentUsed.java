package models.laboratory.experiment.instance;

import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.experiment.description.ReagentType;
import models.utils.HelperObjects;

import org.codehaus.jackson.annotate.JsonIgnore;


public class ReagentUsed {
	
	// Reagent ref
	private String reagentCode;
	// to complete with Map properties or attribut or Values class
	public Map<String,PropertyValue> properties;
	
	@JsonIgnore
	public ReagentType getReagentType() {
		return new HelperObjects<ReagentType>().getObject(ReagentType.class, reagentCode, null);
	}
	 
	
}
