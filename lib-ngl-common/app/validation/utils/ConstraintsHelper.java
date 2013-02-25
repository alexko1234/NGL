package validation.utils;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.data.validation.ValidationError;
import scala.actors.threadpool.Arrays;

public class ConstraintsHelper {
	
	public static void validateProperties(Map<String, List<ValidationError>> errors, Map<String, PropertyValue> properties,List<PropertyDefinition> propertyDefinitions, String rootKeyName) {
		Map<String, PropertyValue> inputProperties = new HashMap<String, PropertyValue>(0);
		if(null != properties){
			inputProperties = new HashMap<String, PropertyValue>(properties);		
		}
		for(PropertyDefinition propertyDefinition: propertyDefinitions){
			PropertyValue pv = inputProperties.get(propertyDefinition.code);

			if(propertyDefinition.active){
				//mandatory
				if(propertyDefinition.required && required(errors, pv, getKey(rootKeyName,propertyDefinition.code))){
					required(errors, pv.value, getKey(rootKeyName,propertyDefinition.code+".value"));
				}
				if(null != pv && null != pv.value){				
					//type	
					try{
						pv = getValue(propertyDefinition, pv);
						properties.put(propertyDefinition.code, pv);
						
						if(propertyDefinition.choiceInList && !checkIfExistInTheList(propertyDefinition, pv)){
							addErrors(errors, getKey(rootKeyName,propertyDefinition.code), "error.valuenotauthorized", pv.value);
						}
						
						//TODO unit validation
						
					}catch(Throwable e){
						addErrors(errors, getKey(rootKeyName,propertyDefinition.code), "error.badtype", propertyDefinition.type, pv.value);
					}
				}	
				
			}else{
				//Non active on les supprimes par defaut pour l'instant et erreur
				if(null != pv){
					properties.remove(propertyDefinition.code);
					//TODO gestion de warning !!!
					addErrors(errors, getKey(rootKeyName,propertyDefinition.code), "error.nonactive");
				}
			}
			
			if(inputProperties.containsKey(propertyDefinition.code)){
				inputProperties.remove(propertyDefinition.code);
			}
		}
		//treat other porperty not defined
		for(String key : inputProperties.keySet()){
			addErrors(errors, getKey(rootKeyName,key), "error.notdefined");
		}
	}

	public static boolean checkIfExistInTheList(
			PropertyDefinition propertyDefinition, PropertyValue pv) {
		for(Value value : propertyDefinition.possibleValues){
			if(pv.value.equals(value.value)){
				return true;
			}			
		}
		return false;
	}

	public static PropertyValue getValue(PropertyDefinition propertyDefinition,
			PropertyValue pv) {
		Class<?> clazz = getClass(propertyDefinition);
		if(null != pv.value && !clazz.isInstance(pv.value)){			
				pv.value = transformValue(propertyDefinition.type, pv.value);				
		}
		return pv;
	}

	public static Class<?> getClass(PropertyDefinition propertyDefinition) {
		Class<?> clazz;
		try {
			clazz = Class.forName(propertyDefinition.type);
		} catch (ClassNotFoundException e) {
			//ne doit pas arriver sauf si objet complexe
			throw new RuntimeException(e);
		}
		return clazz;
	}
	
	/**
	 * 
	 * @param type : final type
	 * @param value : the value
	 * @return
	 */
	public static Object transformValue(String type, Object value) {
		Object o = null;
		if (String.class.getName().equals(type)) {
			o = value.toString();
		} else if (Integer.class.getName().equals(type)) {
			o = Integer.valueOf(value.toString());
		} else if (Double.class.getName().equals(type)) {
			o = Double.valueOf(value.toString());
		} else if (Float.class.getName().equals(type)) {
			o = Float.valueOf(value.toString());
		} else if (Boolean.class.getName().equals(type)) {
			o = Boolean.valueOf(value.toString());
		} else if (Long.class.getName().equals(type)) {
			o = Long.valueOf(value.toString());
		} else if (Date.class.getName().equals(type)) {
			o = new Date(Integer.valueOf(value.toString()));
		} else if (TBoolean.class.getName().equals(type)) {
			o = TBoolean.fromValue(value.toString());
		} else {
			Logger.info("Erreur de type :"+type);
		}
		return o;
	}

	/**
	 * Check if the property is not empty (null, "", " "; size = 0, etc.)
	 * @param errors
	 * @param object
	 * @param key
	 * @return
	 */
	public static boolean required(Map<String, List<ValidationError>> errors, Object object, String key){
		boolean isValid = true;
		if(object == null) {
			isValid =  false;
        }
        if(isValid && object instanceof String) {
        	isValid =  StringUtils.isNotBlank((String)object);
        }
        if(isValid && object instanceof Collection) {
        	isValid =  CollectionUtils.isNotEmpty((Collection)object);
        }
        if(!isValid){
        	addErrors(errors, key, "error.required");
        }        
        return isValid;		
	}
	/**
	 * add an error message
	 * @param errors : list of errors
	 * @param key : property key
	 * @param message : message key
	 * @param arguments : message args
	 */
	@SuppressWarnings("unchecked")
	public static void addErrors(Map<String, List<ValidationError>> errors,
			String key, String message, Object... arguments) {
		if (!errors.containsKey(key)) {
			errors.put(key, new ArrayList<ValidationError>());
		}
		errors.get(key).add(new ValidationError(key, message, Arrays.asList(arguments)));
	}

	public static String getKey(String rootKeyName, String property) {
		return (StringUtils.isBlank(rootKeyName))?property: rootKeyName+"."+property;
	}
}
