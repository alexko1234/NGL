package models.laboratory.experiment.instance;

import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.experiment.description.ReagentType;
import models.utils.ObjectSGBDReference;

import org.codehaus.jackson.annotate.JsonIgnore;


public class ReagentUsed {
	
	// Reagent ref
	private String reagentCode;
	// to complete with Map properties or attribut or Values class
	public Map<String,PropertyValue> properties;
	
	@JsonIgnore
	public ReagentType getReagentType() {
		try {
			//return new ObjectSGBDReference<ReagentType>(ReagentType.class,reagentCode).getObject();
		} catch (Exception e) {
			// TODO
		}
		return null;
	}
	 
	
}
