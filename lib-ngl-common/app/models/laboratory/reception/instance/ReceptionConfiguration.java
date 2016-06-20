package models.laboratory.reception.instance;

import java.util.HashMap;
import java.util.Map;




import models.laboratory.common.instance.TraceInformation;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import fr.cea.ig.DBObject;

public class ReceptionConfiguration extends DBObject implements IValidation{

	public enum FileType {excel};
	public enum Action {save, update};
	
	public String name;
	public FileType fileType;
	public Action action;
	public TraceInformation traceInformation;
	@JsonIgnore
	public Map<String, Map<String, ? extends AbstractFieldConfiguration>> configs = new HashMap<String, Map<String, ? extends AbstractFieldConfiguration>>();

	@JsonAnyGetter
    public Map<String,Map<String,? extends AbstractFieldConfiguration>> configs() {
        return configs;
    }

    @JsonAnySetter
    public void set(String name, Map<String,? extends AbstractFieldConfiguration> value) {
    	configs.put(name, value);
    }
	
	
	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
		
	}

}
