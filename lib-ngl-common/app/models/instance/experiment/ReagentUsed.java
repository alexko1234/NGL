package models.instance.experiment;

import java.util.Map;

import models.description.experiment.ReagentType;
import models.instance.common.PropertyValue;

import org.codehaus.jackson.annotate.JsonIgnore;

import utils.refobject.ObjectSGBDReference;

public class ReagentUsed {
	
	// Reagent ref
	private String reagentCode;
	// to complete with Map properties or attribut or Values class
	public Map<String,PropertyValue> properties;
	
	@JsonIgnore
	public ReagentType getReagentType() {
		try {
			return new ObjectSGBDReference<ReagentType>(ReagentType.class,reagentCode).getObject();
		} catch (Exception e) {
			// TODO
		}
		return null;
	}
	 
	
}
