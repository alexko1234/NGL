package models.laboratory.common.instance.property;

import java.util.Collection;
import java.util.Map;

import validation.utils.ContextValidation;
import validation.utils.ValidationHelper;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;


/**
 * used to stock key : value with value as the same type and unit
 */
public class PropertyMapValue extends PropertyValue<Map<String,?>>{
	
	public PropertyMapValue() {
		super();
	}
	public PropertyMapValue(Map<String, ?> value) {
		super(value);		
	}
	public PropertyMapValue(Map<String, ?> value, String unit) {
		super(value);
		this.unit = unit;
	}
	
	public String unit;
	@Override
	public String toString() {
		return "PropertyMapValue [value=" + value + ", unit=" + unit + ", class="+value.getClass().getName()+"]";
	}
	@Override
	public void validate(ContextValidation contextValidation) {
		PropertyDefinition propertyDefinition = (PropertyDefinition) ((Collection<PropertyDefinition>)contextValidation.getObject("propertyDefinitions")).toArray()[0];
		if(ValidationHelper.checkIfActive(contextValidation, propertyDefinition)){
			if(ValidationHelper.required(contextValidation, this, propertyDefinition)){				
				if(ValidationHelper.convertPropertyValue(contextValidation, this, propertyDefinition)){
					ValidationHelper.checkIfExistInTheList(contextValidation, this, propertyDefinition);
					//TODO FORMAT AND UNIT
				}
			}
		}
		
	}
	

}
