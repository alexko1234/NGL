package models.laboratory.reception.instance;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;




import models.laboratory.common.instance.PropertyValue;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import validation.ContextValidation;




import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;


public class PropertiesFieldConfiguration extends AbstractFieldConfiguration {
	
	@JsonIgnore
	public Map<String, PropertyValueFieldConfiguration> configs = new HashMap<String, PropertyValueFieldConfiguration>();

	@JsonAnyGetter
    public Map<String, PropertyValueFieldConfiguration> configs() {
        return configs;
    }

    @JsonAnySetter
    public void set(String name, PropertyValueFieldConfiguration value) {
    	configs.put(name, value);
    }
	
	public PropertiesFieldConfiguration() {
		super(AbstractFieldConfiguration.propertiesType);		
	}

	@Override
	public void populateField(Field field, Object dbObject,
			Map<Integer, String> rowMap, ContextValidation contextValidation, Action action) throws Exception {
		
		Map<String,PropertyValue> properties = getProperties(field, dbObject, action);
		//we create or update all the properties
		Set<String> propertyNames = configs.keySet();
		propertyNames.forEach(pName -> {
			try {
				PropertyValueFieldConfiguration propertyFieldConfig = configs.get(pName);
				PropertyValue psv = (PropertyValue)Class.forName(propertyFieldConfig.className).newInstance();
				if(null != propertyFieldConfig.value)
					propertyFieldConfig.value.populateField(psv.getClass().getField("value"), psv, rowMap, contextValidation, action);
				if(null != propertyFieldConfig.unit)
					propertyFieldConfig.unit.populateField(psv.getClass().getField("unit"), psv, rowMap, contextValidation, action);
				
				if(null != psv.value){
					properties.put(pName, psv);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}			
		});
		
		populateField(field, dbObject, properties);		
	}

	private Map<String, PropertyValue> getProperties(Field field,
			Object dbObject, Action action)
			throws IllegalAccessException {
		Map<String,PropertyValue> properties = null;
		if(Action.update.equals(action)){
			properties = (Map<String,PropertyValue>) field.get(dbObject);
		}
		
		if(properties == null){
			properties = new HashMap<String, PropertyValue>();			
		}
		return properties;
	}

	
}
