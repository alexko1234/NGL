package models.laboratory.common.instance.property;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import validation.ContextValidation;
import validation.utils.ValidationHelper;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;


/**
 * used to stock a list of complex object
 * an object is stock in Map with key = property and value = value of property
 */
//public class PropertyObjectListValue extends PropertyValue<List<Map<String, ?>>> {
public class PropertyObjectListValue extends PropertyValue<List<Map<String, Object>>> {
	
	public Map<String,String> unit;
	
	public PropertyObjectListValue() {
		super(PropertyValue.objectListType);
	}
	
//	public PropertyObjectListValue(List<Map<String, ?>> value) {
//		super(PropertyValue.objectListType, value);	
//	}
//	public PropertyObjectListValue(List<Map<String, ?>> value, Map<String,String> unit) {
//		super(PropertyValue.objectListType, value);
//		this.unit = unit;
//	}

	public PropertyObjectListValue(List<Map<String, Object>> value) {
		super(PropertyValue.objectListType, value);	
	}
	
	public PropertyObjectListValue(List<Map<String, Object>> value, Map<String,String> unit) {
		super(PropertyValue.objectListType, value);
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "PropertyObjectListValue [value=" + value + ", unit=" + unit + ", class="+value.getClass().getName()+"]";
	}
	
	@Override
	public void validate(ContextValidation contextValidation) {
		super.validate(contextValidation);
		Iterator<PropertyDefinition> propertyDefinitions = ((Collection<PropertyDefinition>)contextValidation.getObject("propertyDefinitions")).iterator();
		while(propertyDefinitions.hasNext()){
			PropertyDefinition propertyDefinition = propertyDefinitions.next();
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyObjectListValue other = (PropertyObjectListValue) obj;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		return true;
	}
	

}
