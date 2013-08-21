package validation.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import play.data.validation.ValidationError;

public class ContextValidation {
	
	public String rootKeyName;
	public Map<String,List<ValidationError>> errors;
	private Map<String,Object> contextObjects;
	
	public ContextValidation(){
		errors= new TreeMap<String, List<ValidationError>>();
		contextObjects= new TreeMap<String, Object>();
	}
	
	public ContextValidation(Map<String,List<ValidationError>> errors){
		this.errors= errors;
		contextObjects= new TreeMap<String, Object>();
	}

	
	public Object getObject(String key){
		if(contextObjects.containsKey(key)){
			return contextObjects.get(key);
		}else{
			return null;
		}
	}
	
	
	public void putObject(String key, Object value){
		contextObjects.put(key, value);
	}
	
	
	/**
	 * add an error message
	 * @param key : property key
	 * @param message : message key
	 * @param arguments : message args
	 */
	public void addErrors(String property, String message, Object...arguments) {
		String key = getKey(property);
		if (!errors.containsKey(key)) {
			errors.put(key, new ArrayList<ValidationError>());
		}		
		errors.get(key).add(new ValidationError(key, message,  java.util.Arrays.asList(arguments)));
	}

	/**
	 * 
	 * @param rootKeyName
	 * @param property
	 * @return
	 */
	public String getKey(String property) {
		return (StringUtils.isBlank(rootKeyName))?property: rootKeyName+"."+property;
	}
}
