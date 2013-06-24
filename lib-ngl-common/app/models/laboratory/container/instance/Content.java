package models.laboratory.container.instance;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;
import validation.utils.BusinessValidationHelper;
import validation.utils.ConstraintsHelper;

import models.laboratory.common.instance.PropertyValue;
import models.utils.IValidation;

public class Content implements IValidation{
	
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

	@JsonIgnore
	@Override
	public void validate(Map<String, List<ValidationError>> errors) {

		if(sampleUsed==null){
			ConstraintsHelper.addErrors(errors,"sampleUsed", "error.codeNotFound");
		}else {
			sampleUsed.validate(errors);
		}
			
	}

}
