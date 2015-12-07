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
public class PropertyObjectListValue extends PropertyValue<List<Map<String, ?>>>{
	
	public PropertyObjectListValue() {
		super();
	}
	public PropertyObjectListValue(List<Map<String, ?>> value) {
		super(value);	
	}
	public PropertyObjectListValue(List<Map<String, ?>> value, Map<String,String> unit) {
		super(value);
		this.unit = unit;
	}
	
	public Map<String,String> unit;
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
					//if(ValidationHelper.checkIfExistInTheList(contextValidation, this, propertyDefinition)){
						ValidationHelper.convertPropertyValue(contextValidation, this, propertyDefinition);
						//TODO FORMAT AND UNIT
					//}
				}
			}
		}
		
	}
	

}
