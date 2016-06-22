package models.laboratory.reception.instance;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import validation.ContextValidation;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;


public class ObjectFieldConfiguration<T> extends AbstractFieldConfiguration {
	
	@JsonIgnore
	public Map<String, AbstractFieldConfiguration> configs = new HashMap<String, AbstractFieldConfiguration>();

	@JsonAnyGetter
    public Map<String, AbstractFieldConfiguration> configs() {
        return configs;
    }

    @JsonAnySetter
    public void set(String name, AbstractFieldConfiguration value) {
    	configs.put(name, value);
    }
	
	public ObjectFieldConfiguration() {
		super(AbstractFieldConfiguration.propertiesType);		
	}

	protected ObjectFieldConfiguration(String type) {
		super(type);
	}

	/**
	 * Populate sub object fields
	 * @param object
	 * @param rowMap
	 * @param contextValidation
	 */
	protected void populateSubFields(Object object, Map<Integer, String> rowMap, ContextValidation contextValidation, Action action) {
		Set<String> propertyNames = configs.keySet();
		propertyNames.forEach(pName -> {
			try {
				AbstractFieldConfiguration afc = configs.get(pName);
				afc.populateField(object.getClass().getField(pName), object, rowMap, contextValidation, action);								
			} catch (Exception e) {
				throw new RuntimeException(e);
			}			
		});
	}

	@Override
	public void populateField(Field field, Object dbObject,
			Map<Integer, String> rowMap, ContextValidation contextValidation, Action action)
			throws Exception {
		
		Object object = field.get(dbObject);
		if(null == object)object = field.getType().newInstance();
		
		populateSubFields(object, rowMap, contextValidation, action);
		populateField(field, dbObject, object);	
	}

	
}
