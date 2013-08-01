package validation.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.data.validation.ValidationError;

public class ContextValidation {
	
	public String rootKeyName;
	public Map<String,List<ValidationError>> errors;
	public Map<String,Object> contextObjects;
	public String key;
	
	public ContextValidation(){
		errors= new HashMap<String, List<ValidationError>>();
		contextObjects= new HashMap<String, Object>();
	}
	
	public ContextValidation(Map<String,List<ValidationError>> errors){
		this.errors= errors;
		contextObjects= new HashMap<String, Object>();
	}

}
