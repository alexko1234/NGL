package models.laboratory.common.instance.property;

import java.util.Collection;

import validation.ContextValidation;
import validation.utils.ValidationHelper;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;


/**
 * Used to stock an array of bytes.
 * 
 *
 */
public class PropertyByteValue extends PropertyValue<byte[]>{
	
	public PropertyByteValue() {
		super();
	}
	public PropertyByteValue(byte[] value) {
		super(value);		
	}
	
	
	@Override
	public String toString() {
		return "PropertyByteValue [value=" + value + ", class="+value.getClass().getName()+"]";
	}
	
	@Override
	public void validate(ContextValidation contextValidation) { 
		super.validate(contextValidation);
		PropertyDefinition propertyDefinition = (PropertyDefinition) ((Collection<PropertyDefinition>)contextValidation.getObject("propertyDefinitions")).toArray()[0];
		if(ValidationHelper.checkIfActive(contextValidation, propertyDefinition)){
			ValidationHelper.required(contextValidation, this, propertyDefinition); 
		}		
	}
	

}
