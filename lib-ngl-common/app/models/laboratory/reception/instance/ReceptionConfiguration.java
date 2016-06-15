package models.laboratory.reception.instance;

import java.util.HashMap;
import java.util.Map;



import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import fr.cea.ig.DBObject;

public class ReceptionConfiguration extends DBObject implements IValidation{

	public String name;
	
	@JsonIgnore
	public Map<String, Map<String, ? extends FieldConfiguration>> configs = new HashMap<String, Map<String, ? extends FieldConfiguration>>();

	@JsonAnyGetter
    public Map<String,Map<String,? extends FieldConfiguration>> configs() {
        return configs;
    }

    @JsonAnySetter
    public void set(String name, Map<String,? extends FieldConfiguration> value) {
    	configs.put(name, value);
    }
	
	
	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
		
	}

}
