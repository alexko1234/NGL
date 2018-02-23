package models.laboratory.reception.instance;

import java.lang.reflect.Field;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import validation.ContextValidation;

public class PropertyValueFieldConfiguration extends AbstractFieldConfiguration {

	// Would probably be better to have the class instead of the name.
	public String className = PropertySingleValue.class.getName();
	public AbstractFieldConfiguration value;
	public AbstractFieldConfiguration unit;
	
	public PropertyValueFieldConfiguration() {
		super(AbstractFieldConfiguration.propertyValueType);		
	}

	@Override
	public void populateField(Field field, Object dbObject,
			                  Map<Integer, String> rowMap, 
			                  ContextValidation contextValidation, 
			                  Action action) throws Exception {
		PropertyValue<?> psv = (PropertyValue<?>)Class.forName(className).newInstance();
		if (value != null)
			value.populateField(psv.getClass().getField("value"), psv, rowMap, contextValidation, action);
		if (unit != null)
			unit.populateField(psv.getClass().getField("unit"), psv, rowMap, contextValidation, action);	
		if (value != null) // only if value not unit
			populateField(field, dbObject, psv);
	}
	
}
