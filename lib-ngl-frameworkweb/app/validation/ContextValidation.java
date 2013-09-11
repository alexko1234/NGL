package validation;

import java.util.ArrayList;
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

	/**
	 * 
	 * @param key
	 * @return
	 */
	public Object getObject(String key){
		if(contextObjects.containsKey(key)) {
			return contextObjects.get(key);
		}
		else {
			return null;
		}
	}
	
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
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
		this.addKeyToRootKeyName(property);
		String key = this.rootKeyName;
		if (!errors.containsKey(key)) {
			errors.put(key, new ArrayList<ValidationError>());
		}		
		errors.get(key).add(new ValidationError(key, message,  java.util.Arrays.asList(arguments)));
		this.removeKeyFromRootKeyName(property);
	}

	/**
	 * 
	 * @param rootKeyName
	 * @param property
	 * @return
	 * @deprecated
	 */
	public String getKey(String property) {
		return (StringUtils.isBlank(rootKeyName))?property: rootKeyName+"."+property;
	}

	
	/**
	 * 
	 * @param property
	 * @return
	 * @deprecated use removeKeyFromRootKeyName
	 */	
	public String removeKey(String property) {
		// TODO Auto-generated method stub
		String strReturn = "";
		if (StringUtils.isBlank(rootKeyName)) {
			strReturn = "";
		}
		if (rootKeyName == property ) {
			strReturn = "";
		}
		else {
			strReturn = rootKeyName.substring(0, rootKeyName.length()-property.length()-1); 
		}
		return strReturn;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRootKeyName() {
		return this.rootKeyName;
	}
	
	/**
	 * 
	 * @param rootKeyName
	 */
	public void setRootKeyName(String rootKeyName) {
		this.rootKeyName = rootKeyName;
	}
	
	/**
	 * 
	 * @param key
	 */
	public void addKeyToRootKeyName(String key) {
		if (StringUtils.isBlank(this.rootKeyName)) {
			this.rootKeyName = key;
		}
		else {
			this.rootKeyName += "." + key;
		}		
	}
	
	/**
	 * 
	 * @param key
	 */
	public void removeKeyFromRootKeyName(String key) {		
		if(StringUtils.isNotBlank(this.rootKeyName) && this.rootKeyName.equals(key)){
			this.rootKeyName = null;
		}else if(StringUtils.isNotBlank(this.rootKeyName) && this.rootKeyName.endsWith(key)){
			this.rootKeyName = this.rootKeyName.substring(0, this.rootKeyName.length()-key.length()-1);
		}		
	}
	
	public boolean hasErrors() {
        return !errors.isEmpty();
    }

}
