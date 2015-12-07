package models.laboratory.common.instance;


import java.util.Collection;

import  com.fasterxml.jackson.annotation.JsonSubTypes;
import  com.fasterxml.jackson.annotation.JsonTypeInfo;
import  com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import  com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import models.laboratory.common.description.PropertyDefinition;
import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;


/**
 * Object used in Map of key/value to stored value with its unit
 * if unit is null, it will not stored in MongoDB
 * 
 * @author mhaquell
 *
 */
@JsonTypeInfo(use=Id.NAME, include=As.EXTERNAL_PROPERTY, property="_type", defaultImpl=models.laboratory.common.instance.property.PropertySingleValue.class, visible=true)
@JsonSubTypes({
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertySingleValue.class, name = "single"),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyListValue.class, name = "list"),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyFileValue.class, name = "file"),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyImgValue.class, name = "img"),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyObjectValue.class, name = "object"),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyObjectListValue.class, name = "object_list")
})
public abstract class PropertyValue<T> implements IValidation {
	
	public String _type;
	public T value;
	
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
	
	public T getValue() {
		return value;
	}
	
	@Override
	public void validate(ContextValidation contextValidation) {
		//Validate type of property against propertyDefinition
		Collection<PropertyDefinition> propertyDefinitions = (Collection<PropertyDefinition>) contextValidation.getObject("propertyDefinitions");
		ValidationHelper.checkType(contextValidation, this, propertyDefinitions);
	}
}
