package models.laboratory.common.instance.property;

import validation.ContextValidation;
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
		// TODO Auto-generated method stub
		
	}
	

}
