package models.laboratory.container.instance;

import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.utils.IValidation;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.utils.ValidationHelper;
import validation.utils.ContextValidation;

public class Content implements IValidation{
		
	public SampleUsed sampleUsed;
	
	// Necessary if not contentType ? Name ?
	// Need to propagate useful properties
	public Map<String,PropertyValue> properties;
	
	public Content(){
		properties=new HashMap<String, PropertyValue>();
	}
	
	@JsonIgnore
	public Content(SampleUsed sampleUsed){
		properties=new HashMap<String, PropertyValue>();
		this.sampleUsed=sampleUsed;
	}

	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		
		if(sampleUsed==null){
			contextValidation.addErrors("sampleUsed", "error.codeNotFound");
		}else {
			sampleUsed.validate(contextValidation);
		}
			
	}

}
