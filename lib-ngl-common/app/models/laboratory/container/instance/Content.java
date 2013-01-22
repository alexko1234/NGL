package models.laboratory.container.instance;

import java.util.Map;

import models.laboratory.common.instance.PropertyValue;

public class Content {
	
	public SampleUsed sample;
	
	// Necessary if not contentType ? Name ?
	// Need to propagate useful properties
	public Map<String,PropertyValue> properties;
	
	public Content(){
		
	}

}
