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
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertySingleValue.class, name = PropertyValue.singleType),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyListValue.class, name = PropertyValue.listType),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyFileValue.class, name = PropertyValue.fileType),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyImgValue.class, name = PropertyValue.imgType),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyObjectValue.class, name = PropertyValue.objectType),
	@JsonSubTypes.Type(value = models.laboratory.common.instance.property.PropertyObjectListValue.class, name = PropertyValue.objectListType)
})
public abstract class PropertyValue<T> implements IValidation {
	
	public static final String singleType = "single";
	public static final String listType = "list";
	public static final String fileType = "file";
	public static final String imgType = "img";
	public static final String objectType = "object";
	public static final String objectListType = "object_list";
	
	public String _type;
	public T value;
	
	public PropertyValue(String _type) {
		super();
		this._type=_type;
	}
	public PropertyValue(String _type, T value) {
		super();
		this._type=_type;
		this.value = value;
	}
	
	
	public PropertyValue(String _type, T value, String unit) {
		super();
		this._type = _type;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_type == null) ? 0 : _type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyValue other = (PropertyValue) obj;
		if (_type == null) {
			if (other._type != null)
				return false;
		} else if (!_type.equals(other._type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	
	
}
