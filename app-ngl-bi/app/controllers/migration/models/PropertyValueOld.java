package controllers.migration.models;


import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

import validation.IValidation;


/**
 * Object used in Map of key/value to stored value with its unit
 * if unit is null, it will not stored in MongoDB
 * 
 * @author mhaquell
 *
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="@type", defaultImpl=controllers.migration.models.property.PropertySingleValueOld.class)
@JsonSubTypes({
	@JsonSubTypes.Type(value = controllers.migration.models.property.PropertySingleValueOld.class, name = "single")
})
public abstract class PropertyValueOld<T> {
	
	public PropertyValueOld() {
		super();
	}
	public PropertyValueOld(T value) {
		super();
		this.value = value;
	}
	public PropertyValueOld(T value, String unit) {
		super();
		this.value = value;		
	}
	public T value;
	
}
