package validation.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.data.validation.ValidationError;

public class ContextValidation {
	
	public String rootKeyName;
	public Map<String,List<ValidationError>> errors;
	List<Object> contextObjects;
	
	public ContextValidation(){
		errors= new HashMap<String, List<ValidationError>>();
	}
	
	public ContextValidation(Map<String,List<ValidationError>> errors){
		this.errors= errors;
	}

}
