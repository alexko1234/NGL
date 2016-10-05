package models.laboratory.reception.instance;

import java.lang.reflect.Field;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
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
			Map<Integer, String> rowMap, ContextValidation contextValidation, Action action) throws Exception {
		
		PropertyValue psv = (PropertyValue)Class.forName(className).newInstance();
		if(null != value)
			value.populateField(psv.getClass().getField("value"), psv, rowMap, contextValidation, action);
		if(null != unit)
			unit.populateField(psv.getClass().getField("unit"), psv, rowMap, contextValidation, action);	
		
		populateField(field, dbObject, psv);
	}

	
}
