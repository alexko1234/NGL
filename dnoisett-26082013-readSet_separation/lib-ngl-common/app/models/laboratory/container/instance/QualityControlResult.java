package models.laboratory.container.instance;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;


import validation.utils.BusinessValidationHelper;
import validation.utils.ContextValidation;

import models.laboratory.common.instance.PropertyValue;
import models.utils.IValidation;


/*
 * Embedded data in collection Container
 * When QualityControle is create/update a copy of result are embedded in container
 * 
 * */
public class QualityControlResult implements IValidation {
	
	public String qualityControleCode;
	public String qualityControleTypeCode;
	public Map<String,PropertyValue> properties;
	
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		
	}
	
}
