package models.laboratory.run.instance;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;

import models.laboratory.common.instance.PropertyValue;


public class Treatment implements IValidation{
	
	public Treatment() {
		super();
	
	}
	
	public Treatment(String typeCode, Map<String, PropertyValue> results) {
		super();
		this.typeCode = typeCode;
		this.results = new HashMap<String, Map<String,PropertyValue>>();
		this.results.put("read1", results);
	}



	public String typeCode;
	public String categoryCode;
	
	@JsonIgnore
	public Map<String, Map<String, PropertyValue>> results = new HashMap<String, Map<String, PropertyValue>>();

	@JsonAnyGetter
    public Map<String,Map<String,PropertyValue>> results() {
        return results;
    }

    @JsonAnySetter
    public void set(String name, Map<String,PropertyValue> value) {
    	results.put(name, value);
    }

	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
		
	}

}
