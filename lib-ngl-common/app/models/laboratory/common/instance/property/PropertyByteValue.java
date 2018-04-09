package models.laboratory.common.instance.property;

import static fr.cea.ig.lfw.utils.Iterables.first;

import java.util.Collection;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import validation.ContextValidation;
import validation.utils.ValidationHelper;


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
	@Override
	public byte[] getValue() {
		return byteValue();
	}
	
	// This overrides the Object definition in the parent class so the 
	// type is effectively a byte array when seen by jackson.
	public void setValue(byte[] b) {
		value = b;
	}
	
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
	
//	public static class Serializer extends StdSerializer<PropertyByteValue> {
//
//	    private static final long serialVersionUID = 1L;
//
//	    public Serializer() {
//	        super(PropertyByteValue.class);
//	    }
//
//	    @Override
//	    public void serialize(PropertyByteValue value, JsonGenerator gen, SerializerProvider provider) throws IOException {
//	        gen.writeString(Base64.encode(value.byteValue()));
//	    }
//	}
//	
//	public static class Deserializer extends StdDeserializer<PropertyByteValue> {
//
//	    private static final long serialVersionUID = 1514703510863497028L;
//
//	    public Deserializer() {
//	        super(PropertyByteValue.class);
//	    }
//
//	    @Override
//	    public PropertyByteValue deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
//	        JsonNode node = p.getCodec().readTree(p);
//	        String base64 = node.asText();
//	        return new PropertyByteValue(Base64.decode(base64));
//	    }
//	}
	
}
