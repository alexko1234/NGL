package models.laboratory.common.instance.property;

import java.util.Collection;

import static fr.cea.ig.lfw.utils.Iterables.first;
import validation.ContextValidation;
import validation.utils.ValidationHelper;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;


/**
 * Property value that is a byte array.
 * 
 *
 */
//public class PropertyByteValue extends PropertyValue<byte[]> {
public class PropertyByteValue extends PropertyValue {
	
	// TODO: Should be protected and define typeless constructors. 
	public PropertyByteValue(String _type) {
		super(_type);
	}
	
	public PropertyByteValue(String _type, byte[] value) {
		super(_type, value);		
	}

	// TODO : activate this method, fails at the moment because the value field holds a String value in some cases.
//	public byte[] getValue() {
//		return byteValue();
//	}
	
	public byte[] byteValue() {
		// If the value is a string we could assume that it is in fact a base64
		// encoded byte array.
		if (! (value instanceof byte[]))
			throw new RuntimeException("value in " + this + " is not a byte[] : " + value.getClass());
		return (byte[])value;
	}
	
	@Override
	public String toString() {
		return "PropertyByteValue [value=" + value + ", class=" + value.getClass().getName() + "]";
	}
	
	@Override
	public void validate(ContextValidation contextValidation) { 
		super.validate(contextValidation);
//		@SuppressWarnings("unchecked") // uncheckable access to validation context object 
//		PropertyDefinition propertyDefinition = (PropertyDefinition) ((Collection<PropertyDefinition>)contextValidation.getObject("propertyDefinitions")).toArray()[0];
		PropertyDefinition propertyDefinition = first(contextValidation.<Collection<PropertyDefinition>>getTypedObject("propertyDefinitions")).orElse(null);
		if (ValidationHelper.checkIfActive(contextValidation, propertyDefinition)) {
			ValidationHelper.required(contextValidation, this, propertyDefinition); 
		}		
	}
	
}
