package models.laboratory.common.instance.property;

import static fr.cea.ig.lfw.utils.Hashing.hash;
import static fr.cea.ig.lfw.utils.Equality.objectEquals;
import static fr.cea.ig.lfw.utils.Equality.typedEquals;
import static fr.cea.ig.lfw.utils.Iterables.first;

import java.util.Collection;

import validation.ContextValidation;
import validation.utils.ValidationHelper;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;


/**
 * Object used in Map of key/value to stored value with its unit
 * if unit is null, it will not stored in MongoDB
 * 
 * @author mhaquell
 *
 */
//public class PropertySingleValue extends PropertyValue<Object> {
public class PropertySingleValue extends PropertyValue {
	
	public String unit;
	
	public PropertySingleValue() {
		super(PropertyValue.singleType);
	}
	
	public PropertySingleValue(Object value) {
		super(PropertyValue.singleType, value);	
	}
	
	public PropertySingleValue(Object value, String unit) {
		super(PropertyValue.singleType, value);
		this.unit = unit;
	}
	
	@Override
	public String toString() {
		return "PropertySingleValue[value=" + value + ", unit=" + unit +  ", class="+value.getClass().getName()+"]";
	}
	
	@Override
	public void validate(ContextValidation contextValidation) {
		super.validate(contextValidation);
//		@SuppressWarnings("unchecked") // Uncheckable access to validation context objects
//		PropertyDefinition propertyDefinition = (PropertyDefinition) ((Collection<PropertyDefinition>)contextValidation.getObject("propertyDefinitions")).toArray()[0];
		PropertyDefinition propertyDefinition = first(contextValidation.<Collection<PropertyDefinition>>getTypedObject("propertyDefinitions")).orElse(null);
		if (ValidationHelper.checkIfActive(contextValidation, propertyDefinition)
				&& ValidationHelper.required(contextValidation, this, propertyDefinition)
				&& ValidationHelper.convertPropertyValue(contextValidation, this, propertyDefinition)) {
			ValidationHelper.checkIfExistInTheList(contextValidation, this, propertyDefinition);
			//TODO FORMAT AND UNIT
		}
	}
	
	@Override
	public int hashCode() {
//		final int prime = 31;
//		int result = super.hashCode();
//		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
//		return result;
		return hash(super.hashCode(), unit);
	}
	
	@Override
	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!super.equals(obj))
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		PropertySingleValue other = (PropertySingleValue) obj;
//		if (unit == null) {
//			if (other.unit != null)
//				return false;
//		} else if (!unit.equals(other.unit))
//			return false;
//		return true;
		return typedEquals(PropertySingleValue.class, this, obj,
				           (x,y) -> super.equals(obj) && objectEquals(x.unit,y.unit));
	}
	
}
