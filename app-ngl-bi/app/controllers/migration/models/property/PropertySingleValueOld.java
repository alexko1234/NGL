package controllers.migration.models.property;

import java.util.Collection;
import java.util.Collections;

import controllers.migration.models.PropertyValueOld;

import validation.ContextValidation;
import validation.utils.ValidationHelper;



/**
 * Object used in Map of key/value to stored value with its unit
 * if unit is null, it will not stored in MongoDB
 * 
 * @author mhaquell
 *
 */
public class PropertySingleValueOld extends PropertyValueOld<Object>{
	
	public PropertySingleValueOld() {
		super();
	}
	public PropertySingleValueOld(Object value) {
		super(value);		
	}
	public PropertySingleValueOld(Object value, String unit) {
		super(value);
		this.unit = unit;
	}
	
	public String unit;
	@Override
	public String toString() {
		return "PropertySingleValue[value=" + value + ", unit=" + unit +  ", class="+value.getClass().getName()+"]";
	}
	

}
