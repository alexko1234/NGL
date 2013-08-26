package models.laboratory.common.instance;

import models.utils.IValidation;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;


/**
 * Object used in Map of key/value to stored value with its unit
 * if unit is null, it will not stored in MongoDB
 * 
 * @author mhaquell
 *
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="@type", defaultImpl=models.laboratory.common.instance.property.PropertySingleValue.class)
@JsonSubTypes({
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertySingleValue.class, name = "PropertySingleValue"),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyListValue.class, name = "PropertyListValue"),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyFileValue.class, name = "PropertyFileValue"),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyMapValue.class, name = "PropertyMapValue"),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyObjectValue.class, name = "PropertyObjectValue"),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyObjectListValue.class, name = "PropertyObjectListValue")
})
public abstract class PropertyValue<T> implements IValidation {
	
	public PropertyValue() {
		super();
	}
	public PropertyValue(T value) {
		super();
		this.value = value;
	}
	public PropertyValue(T value, String unit) {
		super();
		this.value = value;		
	}
	public T value;
	
}
