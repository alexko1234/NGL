package validation.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.property.PropertyByteValue;
import models.laboratory.common.instance.property.PropertyListValue;
import models.laboratory.common.instance.property.PropertyMapValue;
import models.laboratory.common.instance.property.PropertyObjectListValue;
import models.laboratory.common.instance.property.PropertyObjectValue;
import models.laboratory.common.instance.property.PropertySingleValue;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import play.Logger;
import play.data.validation.ValidationError;
import validation.ContextValidation;
import static validation.utils.ValidationConstants.*;

public class ValidationHelper {
	
	/**
	 * 
	 * @param contextValidation
	 * @param properties
	 * @param propertyDefinitions
	 * @param validateNotDefined
	 */
	public static void validateProperties(ContextValidation contextValidation, Map<String, PropertyValue> properties,List<PropertyDefinition> propertyDefinitions, Boolean validateNotDefined) {
		Map<String, PropertyValue> inputProperties = new HashMap<String, PropertyValue>(0);
		if(properties!=null && !properties.isEmpty()){
			inputProperties = new HashMap<String, PropertyValue>(properties);		
		}
		
		Multimap<String, PropertyDefinition> multimap = getMultimap(propertyDefinitions);
		
		for(String key : multimap.keySet()){
			Collection<PropertyDefinition> pdefs = multimap.get(key); 
			PropertyValue pv = inputProperties.get(key);
			
			PropertyDefinition propertyDefinition=(PropertyDefinition) pdefs.toArray()[0];			
			
			//if pv null and required
			if( pv==null && propertyDefinition.required){				
				contextValidation.addErrors(propertyDefinition.code+".value", ERROR_REQUIRED_MSG,"");					
	        	
			}else if (pv != null){
				contextValidation.putObject("propertyDefinitions", pdefs);				
				pv.validate(contextValidation);
			}
			/* old algo not delete please
			for(PropertyDefinition propertyDefinition: pdefs){
				
				if(propertyDefinition.active){
					//mandatory
					if(propertyDefinition.required && required(contextValidation.errors, pv, getKey(rootKeyName,propertyDefinition.code))){
						required(contextValidation.errors, pv.value, getKey(rootKeyName,propertyDefinition.code+".value"));
					}
					if(null != pv && null != pv.value){				
						//type	
						try{
							pv = convertPropertyValue(propertyDefinition, pv);
							properties.put(propertyDefinition.code, pv);
							
							if(propertyDefinition.choiceInList && !checkIfExistInTheList(propertyDefinition, pv.value.toString())){
								addErrors(contextValidation.errors, getKey(rootKeyName,propertyDefinition.code), "error.valuenotauthorized", pv.value);
							}
							
							//TODO unit validation
							
						}catch(Throwable e){
							Logger.error(e.getMessage(),e);
							addErrors(contextValidation.errors, getKey(rootKeyName,propertyDefinition.code), "error.badtype", propertyDefinition.valueType, pv.value);
						}
					}	
					
				}else{
					//Non active on les supprimes par defaut pour l'instant et erreur
					if(null != pv){
						properties.remove(key);
						//TODO gestion de warning !!!
						addErrors(contextValidation.errors, getKey(rootKeyName,propertyDefinition.code), "error.notactive");
					}
				}
				
				
			}
		*/
			//TODO REMOVE NOT ACTIVE IF NOT ACTIVE is a warning and not an error
			if(inputProperties.containsKey(key)){
				inputProperties.remove(key);
			}
		}
		
		//treat other property not defined
		if(validateNotDefined){
			for(String key : inputProperties.keySet()){
				contextValidation.addErrors(key, ERROR_NOTDEFINED_MSG);
			}
		}
	}
	
	/**
	 * transform the list of multimap where the key is the prefix of the code.
	 * 
	 * ex : code = prop.toto the key is prop.
	 * 
	 * Used to manage Object
	 * 
	 * @param propertyDefinitions
	 * @return
	 */
	private static Multimap<String, PropertyDefinition> getMultimap(List<PropertyDefinition> propertyDefinitions) {
		Multimap<String, PropertyDefinition> multimap = ArrayListMultimap.create();		
		for(PropertyDefinition pd : propertyDefinitions){
			multimap.put(splitCodePropertyDefinition(pd)[0], pd);			
		}		
		return multimap;
	}

	/**
	 * 
	 * @param contextValidation
	 * @param properties
	 * @param propertyDefinitions
	 */
	public static void validateProperties(ContextValidation contextValidation, Map<String, PropertyValue> properties,List<PropertyDefinition> propertyDefinitions){
		validateProperties(contextValidation, properties, propertyDefinitions, true);		
	}

	
	/**
	 * 
	 * @param className
	 * @return
	 */
	private static <T> Class<T> getClass(String className) {
		Class<T> clazz;
		try {
			clazz = (Class<T>) Class.forName(className);
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
	 * @param format : format Date
	 * @return
	 * @deprecated used convertValue(Class type, String value,String inputFormat)
	 */
	public static Object transformValue(String type, Object value,String inputFormat) {
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
			if(inputFormat==null){
				o = new Date(Long.valueOf(value.toString()));
			} else {  
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(inputFormat);
				try {
					o=simpleDateFormat.parse(value.toString());
				} catch (ParseException e) {
					throw new RuntimeException(e.getMessage(),e);
				}
			}
		} else if (TBoolean.class.getName().equals(type)) {
			o = TBoolean.valueOf(value.toString());
		} else {
			Logger.info("Erreur de type :"+type);
			throw new RuntimeException("Type not managed: "+type);
		}
		return o;
	}

	/**
	 * Convert a String to the good object
	 * @param type
	 * @param value
	 * @return
	 */
	public static Object convertStringToType(String type, String value){
		try{
			Class<?> valueClass = getClass(type);
			return convertValue(valueClass, value, null);
		}catch(Throwable e){
			Logger.error(e.getMessage(),e);			
		}
		return null;		
	}
	
	public static List<Object> convertStringToType(String type, List<String> values){
		try{
			Class<?> valueClass = getClass(type);
			List<Object> objects = new ArrayList<Object>(values.size());
			for(String value : values){
				objects.add(convertValue(valueClass, value, null));
			}			
			return objects;
		}catch(Throwable e){
			Logger.error(e.getMessage(),e);			
		}
		return null;		
	}
	
	/**
	 * 
	 * @param type : final type
	 * @param value : the value
	 * @param format : format Date
	 * @return
	 */
	public static Object convertValue(Class<?> type, String value, String inputFormat) {
		Object o = null;
		if (String.class.equals(type)) {
			o = value;
		} else if (Integer.class.equals(type)) {
			o = Integer.valueOf(value);
		} else if (Double.class.equals(type)) {
			o = Double.valueOf(value);
		} else if (Float.class.equals(type)) {
			o = Float.valueOf(value);
		} else if (Boolean.class.equals(type)) {
			o = Boolean.valueOf(value);
		} else if (Long.class.equals(type)) {
			o = Long.valueOf(value);
		} else if (Date.class.equals(type)) {
			if(inputFormat==null){
				o = new Date(Long.valueOf(value));
			} else {  
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(inputFormat);
				try {
					o=simpleDateFormat.parse(value);
				} catch (ParseException e) {
					throw new RuntimeException(e.getMessage(),e);
				}
			}
		} else if (TBoolean.class.equals(type)) {
			o = TBoolean.valueOf(value);
		} else {
			Logger.info("Erreur de type :"+type);
			throw new RuntimeException("Type not managed: "+type);
		}
		return o;
	}
	
	/**
	 * Check if the property is not empty (null, "", " "; size = 0, etc.)
	 * @param errors
	 * @param object
	 * @param key
	 * @return
	 * @deprecated
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
        
        if(isValid && object instanceof Map) {
        	isValid =  MapUtils.isNotEmpty((Map)object);
        }
        
        if(!isValid){
        	addErrors(errors, key, ERROR_REQUIRED_MSG,object);
        }        
        return isValid;		
	}
	
	/**
	 * add an error message
	 * @param errors : list of errors
	 * @param key : property key
	 * @param message : message key
	 * @param arguments : message args
	 * @deprecated used ContextValidation.addErrors
	 */
	public static void addErrors(Map<String, List<ValidationError>> errors,
			String key, String message, Object... arguments) {
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
	 * @deprecated used ContextValidation.getKey
	 */
	public static String getKey(String rootKeyName, String property) {
		return (StringUtils.isBlank(rootKeyName))?property: rootKeyName+"."+property;
	}
	
	

	/**
	 * Transform the value of propertyValue to the good type
	 * @param propertyValue
	 * @param propertyDefinition
	 * @param contextValidation
	 * @return
	 */
	public static boolean convertPropertyValue(ContextValidation contextValidation, PropertySingleValue propertyValue, PropertyDefinition propertyDefinition) {
		try{
			Class<?> valueClass = getClass(propertyDefinition.valueType);
			if(!valueClass.isInstance(propertyValue.value)){ //transform only if not the good type
				propertyValue.value = convertValue(valueClass, propertyValue.value.toString(), null);
			}
		}catch(Throwable e){
			Logger.error(e.getMessage(),e);
			contextValidation.addErrors(propertyDefinition.code+".value", ERROR_BADTYPE_MSG, propertyDefinition.valueType, propertyValue.value);
			return false;
		}
		return true;
	}
	
	/**
	 * Transform the value of propertyValue to the good type
	 * @param propertyValue
	 * @param propertyDefinition
	 * @param contextValidation
	 * @return
	 */
	public static boolean convertPropertyValue(ContextValidation contextValidation, PropertyListValue propertyValue, PropertyDefinition propertyDefinition) {
		try{
			Class<?> valueClass = getClass(propertyDefinition.valueType);
			List<Object> newList = new ArrayList<Object>(propertyValue.value.size());
			for(Object value : propertyValue.value){
				if(!valueClass.isInstance(value)){ //transform only if not the good type
					value = convertValue(valueClass, value.toString(), null);
				}
				newList.add(value);
			}			
			propertyValue.value = newList;
			if(propertyDefinition.saveMeasureValue!=null){
				propertyValue.unit=propertyDefinition.saveMeasureValue.value; }
		}catch(Throwable e){
			Logger.error(e.getMessage(),e);
			contextValidation.addErrors(propertyDefinition.code, ERROR_BADTYPE_MSG, propertyDefinition.valueType, propertyValue.value);
			return false;
		}
		return true;
	}
	
	/**
	 * Transform the value of propertyValue to the good type
	 * @param propertyValue
	 * @param propertyDefinition
	 * @param contextValidation
	 * @return
	 */
	public static boolean convertPropertyValue(ContextValidation contextValidation, PropertyMapValue propertyValue, PropertyDefinition propertyDefinition) {
		try{
			Class<?> valueClass = getClass(propertyDefinition.valueType);
			Map<String, Object> newMap = new HashMap<String, Object>(propertyValue.value.size());
			for(Entry<String,? extends Object> entryValue : propertyValue.value.entrySet()){
				Object value = entryValue.getValue();
				if(!valueClass.isInstance(value)){ //transform only if not the good type
					value = convertValue(valueClass, value.toString(), null);
				}
				newMap.put(entryValue.getKey(), value);
			}			
			propertyValue.value = newMap;
		}catch(Throwable e){
			Logger.error(e.getMessage(),e);
			contextValidation.addErrors(propertyDefinition.code, ERROR_BADTYPE_MSG, propertyDefinition.valueType, propertyValue.value);
			return false;
		}
		return true;
	}
	
	/**
	 * Transform the value of propertyValue to the good type
	 * @param propertyValue
	 * @param propertyDefinition
	 * @param contextValidation
	 * @return
	 */
	public static boolean convertPropertyValue(ContextValidation contextValidation, PropertyObjectValue propertyValue, PropertyDefinition propertyDefinition) {
		String[] codes = splitCodePropertyDefinition(propertyDefinition);
		try{
			Class<?> valueClass = getClass(propertyDefinition.valueType);
			Map<String, Object> map = (Map<String, Object>) propertyValue.value;
			Object value = map.get(codes[1]);
			
			if(!valueClass.isInstance(value)){ //transform only if not the good type
				value = convertValue(valueClass, value.toString(), null);
			}	
			map.put(codes[1], value);			

		}catch(Throwable e){
			Logger.error(e.getMessage(),e);
			contextValidation.addErrors(codes[0]+".value."+codes[1], ERROR_BADTYPE_MSG, propertyDefinition.valueType, propertyValue.value);
			return false;
		}
		return true;
	}
	
	/**
	 * Transform the value of propertyValue to the good type
	 * @param propertyValue
	 * @param propertyDefinition
	 * @param contextValidation
	 * @return
	 */
	public static boolean convertPropertyValue(ContextValidation contextValidation, PropertyObjectListValue propertyValue, PropertyDefinition propertyDefinition) {
		String[] codes = splitCodePropertyDefinition(propertyDefinition);
		try{
			Class<?> valueClass = getClass(propertyDefinition.valueType);
			List<Map<String, ?>> list = propertyValue.value;
			
			for(Map<String, ?> map: list){
				Object value = map.get(codes[1]);
				if(!valueClass.isInstance(value) && value!=null){ //transform only if not the good type
					value = convertValue(valueClass, value.toString(), null);
				}	
				((Map<String, Object>)map).put(codes[1], value);
			}
			
		}catch(Throwable e){
			Logger.error(e.getMessage(),e);
			contextValidation.addErrors(codes[0]+".value."+codes[1], ERROR_BADTYPE_MSG, propertyDefinition.valueType, propertyValue.value);
			return false;
		}
		return true;
	}
	
	/**
	 * Check if propertyValue is required
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return
	 */
	public static boolean required(ContextValidation contextValidation, PropertySingleValue propertyValue, PropertyDefinition propertyDefinition){
		if(propertyDefinition.required && required(contextValidation, propertyValue, propertyDefinition.code)){
			return required(contextValidation, propertyValue.value, propertyDefinition.code+".value");
		}else if(propertyDefinition.required){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * Check if propertyValue is required
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return
	 */
	public static boolean required(ContextValidation contextValidation, PropertyListValue propertyValue, PropertyDefinition propertyDefinition){
		if(propertyDefinition.required && required(contextValidation, propertyValue, propertyDefinition.code)){
			boolean isValid = true;
			if(CollectionUtils.isNotEmpty(propertyValue.value)){
				int i = 0;
				for(Object value : propertyValue.value){
					if(!required(contextValidation, value, propertyDefinition.code+".value["+i+++"]")){
						isValid = false;
					}
				}	
	        }else{
	        	isValid = false;
	        	contextValidation.addErrors(propertyDefinition.code+".value", ERROR_REQUIRED_MSG,propertyValue.value);
	        }			
			return isValid;
		}else if(propertyDefinition.required){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * Check if propertyValue is required
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return
	 */
	public static boolean required(ContextValidation contextValidation, PropertyMapValue propertyValue, PropertyDefinition propertyDefinition){
		if(propertyDefinition.required && required(contextValidation, propertyValue, propertyDefinition.code)){
			boolean isValid = true;
			if(MapUtils.isNotEmpty(propertyValue.value)){
				for(Entry<String, ?> entryValue : propertyValue.value.entrySet()){
					if(!required(contextValidation, entryValue.getValue(), propertyDefinition.code+".value."+entryValue.getKey())){
						isValid = false;
					}
				}	
	        }else{
	        	isValid = false;
	        	contextValidation.addErrors(propertyDefinition.code+".value", ERROR_REQUIRED_MSG,propertyValue.value);
	        }			
			return isValid;
		}else if(propertyDefinition.required){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * Check if propertyValue is required
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return
	 */
	public static boolean required(ContextValidation contextValidation, PropertyObjectValue propertyValue, PropertyDefinition propertyDefinition){
		if(propertyDefinition.required && required(contextValidation, propertyValue, propertyDefinition.code)){
			
			String[] codes = splitCodePropertyDefinition(propertyDefinition);
			Map<String, Object> map = (Map<String, Object>) propertyValue.value;
			Object value = map.get(codes[1]);
			
			return required(contextValidation, value, codes[0]+".value."+codes[1]);
		}else if(propertyDefinition.required){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * Check if propertyValue is required
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return
	 */
	public static boolean required(ContextValidation contextValidation, PropertyObjectListValue propertyValue, PropertyDefinition propertyDefinition){
		if(propertyDefinition.required && required(contextValidation, propertyValue, propertyDefinition.code)){
			String[] codes = splitCodePropertyDefinition(propertyDefinition);
			List<Map<String, ?>> list = (List<Map<String, ?>>) propertyValue.value;
			int i = 0;
			boolean isValid = true;
			for(Map<String, ?> map: list){
				Object value = map.get(codes[1]);
				if(!required(contextValidation, value, codes[0]+".value["+i+++"]."+codes[1])){
					isValid = false;
				}
			}		
			return isValid;
		}else if(propertyDefinition.required){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return
	 */
	public static boolean required(ContextValidation contextValidation, PropertyByteValue propertyValue, PropertyDefinition propertyDefinition){
		if(propertyDefinition.required && required(contextValidation, propertyValue, propertyDefinition.code)){
			return required(contextValidation, propertyValue.value, propertyDefinition.code+".value");
		}else if(propertyDefinition.required){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * Split the propertyDefinition code with "." if exist
	 * @param propertyDefinition
	 * @return
	 */
	private static String[] splitCodePropertyDefinition(
			PropertyDefinition propertyDefinition) {
		return propertyDefinition.code.split("\\.", 2);
	}
	
	/**
	 * Check if the property is not empty (null, "", " "; size = 0, etc.)
	 * @param object
	 * @param key
	 * @return
	 */
	public static boolean required(ContextValidation contextValidation, Object object, String property){
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
        
        if(isValid && object instanceof Map) {
        	isValid =  MapUtils.isNotEmpty((Map)object);        	
        }
        
        if(isValid && object instanceof byte[]) {
        	byte[] byteArrayObject =  (byte[]) object;
        	isValid = (byteArrayObject.length==0?false:true);
        }
        
        if(!isValid){
        	contextValidation.addErrors(property, ERROR_REQUIRED_MSG,object);
        }        
        return isValid;		
	}
	
	
	
	/**
	 * Check if the value is in the list
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return
	 */
	public static boolean checkIfExistInTheList(ContextValidation contextValidation, PropertySingleValue propertyValue, PropertyDefinition propertyDefinition){
		if(propertyDefinition.choiceInList && !checkIfExistInTheList(propertyDefinition, propertyValue.value.toString())){
			contextValidation.addErrors(propertyDefinition.code+".value", ERROR_VALUENOTAUTHORIZED_MSG, propertyValue.value);
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * Check if the value is in the list
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return
	 */
	public static boolean checkIfExistInTheList(ContextValidation contextValidation, PropertyListValue propertyValue, PropertyDefinition propertyDefinition){
		if(propertyDefinition.choiceInList){
			int i = 0;
			for(Object value : propertyValue.value){
				if(!checkIfExistInTheList(propertyDefinition, value.toString())){
					contextValidation.addErrors(propertyDefinition.code+".value["+i+++"]", ERROR_VALUENOTAUTHORIZED_MSG, value);
				}
			}
			
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * Check if the value is in the list
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return
	 */
	public static boolean checkIfExistInTheList(ContextValidation contextValidation, PropertyMapValue propertyValue, PropertyDefinition propertyDefinition){
		if(propertyDefinition.choiceInList){
			for(Entry<String, ?> entryValue : propertyValue.value.entrySet()){
				Object value = entryValue.getValue();
				if(!checkIfExistInTheList(propertyDefinition, value.toString())){
					contextValidation.addErrors(propertyDefinition.code+".value."+entryValue.getKey(), ERROR_VALUENOTAUTHORIZED_MSG, value);
				}
			}			
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 */
	public static boolean checkIfExistInTheList(ContextValidation contextValidation, PropertyObjectValue propertyValue, PropertyDefinition propertyDefinition) {
		if(propertyDefinition.choiceInList){
			for(Entry<String, ?> entryValue : propertyValue.value.entrySet()){
				Object value = entryValue.getValue();
				if((propertyDefinition.code.endsWith(entryValue.getKey())) && !checkIfExistInTheList(propertyDefinition, value.toString())){
					contextValidation.addErrors(propertyDefinition.code+".value."+entryValue.getKey(), ERROR_VALUENOTAUTHORIZED_MSG, value);
				}
			}			
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 */
	public static void checkIfExistInTheList(ContextValidation contextValidation, PropertyObjectListValue propertyValue, PropertyDefinition propertyDefinition) {
		//TODO !
		Logger.error("checkIfExistInTheList not implemented");
	}
	
	/**
	 * 
	 * @param propertyDefinition
	 * @param value
	 * @return
	 */
	public static boolean checkIfExistInTheList(
			PropertyDefinition propertyDefinition, String value) {
		for(Value possibleValue : propertyDefinition.possibleValues){
			if(value.equals(possibleValue.code)){
				return true;
			}			
		}
		return false;
	}
	
	/**
	 * Check if the propertyDefinition is active
	 * @param contextValidation
	 * @param propertyDefinition
	 * @return
	 */
	public static boolean checkIfActive(ContextValidation contextValidation, PropertyDefinition propertyDefinition){
		if(propertyDefinition.active){
			return true;
		}else{
			String[] codes = splitCodePropertyDefinition(propertyDefinition);
			if(codes.length == 1){ //simple case
				contextValidation.addErrors(propertyDefinition.code, ERROR_NOTACTIVE);
			}else{ // object case
				contextValidation.addErrors(codes[0]+".value."+codes[1], ERROR_NOTACTIVE);
			}
			return false;			
		}
	}

	
}
