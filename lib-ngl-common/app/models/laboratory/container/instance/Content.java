package models.laboratory.container.instance;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;


import validation.utils.BusinessValidationHelper;
import validation.utils.ConstraintsHelper;
import validation.utils.ContextValidation;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.utils.IValidation;

public class Content implements IValidation{
	
	
	@JsonIgnore
	public final static String LEVEL_SEARCH=Level.CODE.Content.toString();
	
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
			ConstraintsHelper.addErrors(contextValidation.errors,"sampleUsed", "error.codeNotFound");
		}else {
			sampleUsed.validate(contextValidation);
		}
			
	}

}
