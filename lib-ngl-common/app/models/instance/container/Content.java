package models.instance.container;

import java.util.Map;

import models.instance.common.PropertyValue;

public class Content {
	
	public SampleUsed sample;
	
	// Necessary if not contentType ? Name ?
	// Need to propagate useful properties
	public Map<String,PropertyValue> properties;
	
	public Content(){
		
	}

}
