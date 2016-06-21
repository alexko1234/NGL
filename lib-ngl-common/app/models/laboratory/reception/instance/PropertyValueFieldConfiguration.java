package models.laboratory.reception.instance;

import java.lang.reflect.Field;
import java.util.Map;

import models.laboratory.common.instance.property.PropertySingleValue;
import validation.ContextValidation;


public class PropertyValueFieldConfiguration extends AbstractFieldConfiguration {
	
	public String className = PropertySingleValue.class.getName();
	public AbstractFieldConfiguration value;
	public AbstractFieldConfiguration unit;
	
	public PropertyValueFieldConfiguration() {
		super(AbstractFieldConfiguration.propertyValueType);		
	}

	@Override
	public void populateField(Field field, Object dbObject,
			Map<Integer, String> rowMap, ContextValidation contextValidation) throws Exception {
		//Manage in PropertiesFieldConfiguration because we are not field on Map
	}

	
}
