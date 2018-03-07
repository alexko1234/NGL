package models.laboratory.common.instance.property;

import static fr.cea.ig.lfw.utils.Hashing.hash;
import static fr.cea.ig.lfw.utils.Equality.objectEquals;
import static fr.cea.ig.lfw.utils.Equality.typedEquals;

import java.util.Collection;
import java.util.List;

import validation.ContextValidation;
import validation.utils.ValidationHelper;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;


/**
 *
 */
//public class PropertyListValue extends PropertyValue<List<? extends Object>>{
public class PropertyListValue extends PropertyValue {
	
	public String unit;
	
	public PropertyListValue() {
		super(PropertyValue.listType);
	}
	
	public PropertyListValue(List<? extends Object> value) {
		super(PropertyValue.listType, value);
	}
	
	public PropertyListValue(List<? extends Object> value, String unit) {
		super(PropertyValue.listType, value);
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "PropertyListValue [value=" + value + ", unit=" + unit + ", class=" + value.getClass().getName() + "]";
	}
	
	@Override
	public void validate(ContextValidation contextValidation) {
		super.validate(contextValidation);
		@SuppressWarnings("unchecked") // cannot access validation context object without a cast
		PropertyDefinition propertyDefinition = (PropertyDefinition) ((Collection<PropertyDefinition>)contextValidation.getObject("propertyDefinitions")).toArray()[0];
		if(ValidationHelper.checkIfActive(contextValidation, propertyDefinition)){
			if(ValidationHelper.required(contextValidation, this, propertyDefinition)){				
				if(ValidationHelper.convertPropertyValue(contextValidation, this, propertyDefinition)){
					ValidationHelper.checkIfExistInTheList(contextValidation, this, propertyDefinition);
					//TODO FORMAT AND UNIT
				}
			}
		}
		
	}
	
	@SuppressWarnings("unchecked") // value cannot be properly typed unless value is moved out of PropertyValue 
	public List<Object> listValue() {
		return (List<Object>)value;
	}
	
	@Override
	public int hashCode() {
//		final int prime = 31;
//		int result = super.hashCode();
//		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
//		return result;
		return hash(super.hashCode(),unit);
	}
	
	@Override
	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!super.equals(obj))
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		PropertyListValue other = (PropertyListValue) obj;
//		if (unit == null) {
//			if (other.unit != null)
//				return false;
//		} else if (!unit.equals(other.unit))
//			return false;
//		return true;
		return typedEquals(PropertyListValue.class, this, obj,
				           (x,y) -> super.equals(obj) && objectEquals(x.unit,y.unit));
	}
	
}
