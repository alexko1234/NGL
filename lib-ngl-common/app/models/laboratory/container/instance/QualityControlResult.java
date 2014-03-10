package models.laboratory.container.instance;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;


import validation.ContextValidation;
import validation.IValidation;
import validation.utils.BusinessValidationHelper;

import models.laboratory.common.instance.PropertyValue;


/*
 * Embedded data in collection Container
 * When QualityControle is create/update a copy of result are embedded in container
 * 
 * */
public class QualityControlResult implements IValidation {
	
	public String code;
	public String typeCode;
	public Map<String,PropertyValue> properties;
	
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		
	}
	
}
