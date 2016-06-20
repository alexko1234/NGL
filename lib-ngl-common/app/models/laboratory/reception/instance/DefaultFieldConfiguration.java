package models.laboratory.reception.instance;

import java.lang.reflect.Field;
import java.util.Map;

import validation.ContextValidation;
import fr.cea.ig.DBObject;

public class DefaultFieldConfiguration extends AbstractFieldConfiguration {
	public String value;

	public DefaultFieldConfiguration() {
		super(AbstractFieldConfiguration.defaultType);		
	}

	@Override
	public void populateField(Field field, DBObject dbObject,
			AbstractFieldConfiguration fieldConfiguration,
			Map<Integer, String> rowMap, ContextValidation contextValidation) throws Exception {
		populateField(field, dbObject, value);		
	}

	
}
