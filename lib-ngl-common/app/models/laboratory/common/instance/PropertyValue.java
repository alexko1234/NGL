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
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertySingleValue.class, name = "single"),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyListValue.class, name = "list"),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyFileValue.class, name = "file"),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyMapValue.class, name = "map"),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyObjectValue.class, name = "object"),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyObjectListValue.class, name = "object_list")
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
