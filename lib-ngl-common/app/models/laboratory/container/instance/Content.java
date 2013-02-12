package models.laboratory.container.instance;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import models.laboratory.common.instance.PropertyValue;

public class Content {
	
	public SampleUsed sampleUsed;
	
	// Necessary if not contentType ? Name ?
	// Need to propagate useful properties
	public Map<String,PropertyValue> properties;
	
	public Content(){
		
	}
	
	@JsonIgnore
	public Content(SampleUsed sampleUsed){
		this.sampleUsed=sampleUsed;
	}

}
