package models.laboratory.reception.instance;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import play.Logger;
import validation.ContextValidation;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.cea.ig.DBObject;

public class PropertiesFieldConfiguration extends AbstractFieldConfiguration {
	
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
	
	public PropertiesFieldConfiguration() {
		super(AbstractFieldConfiguration.propertiesType);		
	}

	@Override
	public void populateField(Field field, DBObject dbObject,
			AbstractFieldConfiguration fieldConfiguration,
			Map<Integer, String> rowMap, ContextValidation contextValidation) {
		Logger.error("Not Implemented");
		
	}

	
}
