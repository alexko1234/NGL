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
import java.util.stream.Collectors;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.State;
import models.laboratory.common.description.Value;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.property.PropertyByteValue;
import models.laboratory.common.instance.property.PropertyListValue;
import models.laboratory.common.instance.property.PropertyObjectListValue;
import models.laboratory.common.instance.property.PropertyObjectValue;
import models.laboratory.common.instance.property.PropertySingleValue;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

//import play.Logger;
import play.data.validation.ValidationError;
import validation.ContextValidation;
import static validation.utils.ValidationConstants.*;

public class ValidationHelper {
	
	public static final play.Logger.ALogger logger = play.Logger.of(ValidationHelper.class);
	
//	public static void validateProperties(ContextValidation contextValidation, Map<String, PropertyValue<?>> properties,List<PropertyDefinition> propertyDefinitions, Boolean validateNotDefined) {
//		validateProperties(contextValidation, properties, propertyDefinitions, validateNotDefined, true, null, null);
//	}
	public static void validateProperties(ContextValidation contextValidation, Map<String, PropertyValue> properties,List<PropertyDefinition> propertyDefinitions, Boolean validateNotDefined) {
		validateProperties(contextValidation, properties, propertyDefinitions, validateNotDefined, true, null, null);
	}
	
	/*
	 * 
	 * @param contextValidation
	 * @param properties
	 * @param propertyDefinitions
	 */
//	public static void validateProperties(ContextValidation contextValidation, Map<String, PropertyValue<?>> properties,List<PropertyDefinition> propertyDefinitions){
//		validateProperties(contextValidation, properties, propertyDefinitions, true, true, null, null);		
//	}
	public static void validateProperties(ContextValidation contextValidation, Map<String, PropertyValue> properties,List<PropertyDefinition> propertyDefinitions){
		validateProperties(contextValidation, properties, propertyDefinitions, true, true, null, null);		
	}
	
	/*
	 * 
	 * @param contextValidation
	 * @param properties
	 * @param propertyDefinitions
	 * @param validateNotDefined
	 */
//	public static void validateProperties(ContextValidation contextValidation, Map<String, PropertyValue<?>> properties,List<PropertyDefinition> propertyDefinitions, Boolean validateNotDefined, Boolean testRequired, String currentStateCode, String defaultRequiredState) {
//		Map<String, PropertyValue<?>> inputProperties = new HashMap<>(); //<String, PropertyValue>(0);
//		if (properties != null && !properties.isEmpty()) {
//			cleanningProperties(properties);
//			inputProperties = new HashMap<>(); // String, PropertyValue>(properties);		
//		}		
//		Multimap<String, PropertyDefinition> multimap = getMultimap(propertyDefinitions);
//		
//		for(String key : multimap.keySet()){
//			Collection<PropertyDefinition> pdefs = multimap.get(key); 
//			PropertyValue<?> pv = inputProperties.get(key);
//			
//			PropertyDefinition propertyDefinition=(PropertyDefinition) pdefs.toArray()[0];			
//			
//			//if pv null and required
//			if(pv == null && propertyDefinition.required && testRequired 
//					&& isStateRequired(currentStateCode, propertyDefinition.requiredState, defaultRequiredState)){				
//				contextValidation.addErrors(propertyDefinition.code+".value", ERROR_REQUIRED_MSG,"");					
//	        	
//			} else if (pv != null) {
//				contextValidation.putObject("propertyDefinitions", pdefs);				
//				pv.validate(contextValidation);
//			}
//			/* old algo not delete please
//			for(PropertyDefinition propertyDefinition: pdefs){
//				
//				if(propertyDefinition.active){
//					//mandatory
//					if(propertyDefinition.required && required(contextValidation.errors, pv, getKey(rootKeyName,propertyDefinition.code))){
//						required(contextValidation.errors, pv.value, getKey(rootKeyName,propertyDefinition.code+".value"));
//					}
//					if(null != pv && null != pv.value){				
//						//type	
//						try{
//							pv = convertPropertyValue(propertyDefinition, pv);
//							properties.put(propertyDefinition.code, pv);
//							
//							if(propertyDefinition.choiceInList && !checkIfExistInTheList(propertyDefinition, pv.value.toString())){
//								addErrors(contextValidation.errors, getKey(rootKeyName,propertyDefinition.code), "error.valuenotauthorized", pv.value);
//							}
//							
//							//TODO unit validation
//							
//						}catch(Throwable e){
//							Logger.error(e.getMessage(),e);
//							addErrors(contextValidation.errors, getKey(rootKeyName,propertyDefinition.code), "error.badtype", propertyDefinition.valueType, pv.value);
//						}
//					}	
//					
//				}else{
//					//Non active on les supprimes par defaut pour l'instant et erreur
//					if(null != pv){
//						properties.remove(key);
//						//TODO gestion de warning !!!
//						addErrors(contextValidation.errors, getKey(rootKeyName,propertyDefinition.code), "error.notactive");
//					}
//				}
//				
//				
//			}
//		*/
//			//TODO REMOVE NOT ACTIVE IF NOT ACTIVE is a warning and not an error
//			if(inputProperties.containsKey(key)){
//				inputProperties.remove(key);
//			}
//		}
//		
//		//treat other property not defined
//		if(validateNotDefined){
//			for(String key : inputProperties.keySet()){
//				contextValidation.addErrors(key, ERROR_NOTDEFINED_MSG);
//			}
//		}
//	}
	
	public static void validateProperties(ContextValidation contextValidation, Map<String, PropertyValue> properties,List<PropertyDefinition> propertyDefinitions, Boolean validateNotDefined, Boolean testRequired, String currentStateCode, String defaultRequiredState) {
		Map<String, PropertyValue> inputProperties = new HashMap<>(); //<String, PropertyValue>(0);
		if (properties != null && !properties.isEmpty()) {
			cleanningProperties(properties);
			inputProperties = new HashMap<>(); // String, PropertyValue>(properties);		
		}		
		Multimap<String, PropertyDefinition> multimap = getMultimap(propertyDefinitions);
		
		for(String key : multimap.keySet()){
			Collection<PropertyDefinition> pdefs = multimap.get(key); 
			PropertyValue pv = inputProperties.get(key);
			PropertyDefinition propertyDefinition = (PropertyDefinition) pdefs.toArray()[0];			
			//if pv null and required
			if (pv == null && propertyDefinition.required 
					       && testRequired 
				           && isStateRequired(currentStateCode, propertyDefinition.requiredState, defaultRequiredState)) {				
				contextValidation.addErrors(propertyDefinition.code+".value", ERROR_REQUIRED_MSG,"");					
			} else if (pv != null) {
				contextValidation.putObject("propertyDefinitions", pdefs);				
				pv.validate(contextValidation);
			}
			//TODO REMOVE NOT ACTIVE IF NOT ACTIVE is a warning and not an error
			if(inputProperties.containsKey(key)){
				inputProperties.remove(key);
			}
		}		
		//treat other property not defined
		if (validateNotDefined) {
			for(String key : inputProperties.keySet()){
				contextValidation.addErrors(key, ERROR_NOTDEFINED_MSG);
			}
		}
	}
	
//	private static void cleanningProperties(Map<String, PropertyValue<?>> properties) {
//		List<String> removedKeys = properties.entrySet().parallelStream()
//			.filter(entry -> (entry.getValue() == null || entry.getValue().value == null || StringUtils.isBlank(entry.getValue().value.toString())))
//			.map(entry -> entry.getKey())
//			.collect(Collectors.toList());
//		if (!removedKeys.isEmpty()) {
//			removedKeys.parallelStream().forEach(key -> properties.remove(key));
//		}		
//	}
	private static void cleanningProperties(Map<String, PropertyValue> properties) {
		List<String> removedKeys = properties.entrySet().parallelStream()
			.filter(entry -> (entry.getValue() == null || entry.getValue().value == null || StringUtils.isBlank(entry.getValue().value.toString())))
			.map(entry -> entry.getKey())
			.collect(Collectors.toList());
		if (!removedKeys.isEmpty()) {
			removedKeys.parallelStream().forEach(key -> properties.remove(key));
		}		
	}

	private static boolean isStateRequired(String currentStateCode,	String requiredState, String defaultRequiredState) {
		if (currentStateCode== null || (null == defaultRequiredState && null == requiredState)){
			return true;
		} else {
			State currentState = State.find.findByCode(currentStateCode);
			State pdRequiredState = State.find.findByCode((requiredState == null)?defaultRequiredState:requiredState);
			return currentState.position >= pdRequiredState.position;
		}						
	}

	/*
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

	

	
	/*
	 * 
	 * @param className
	 * @return
	 */
	@SuppressWarnings("unchecked")
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
	
	/*
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
			logger.info("Erreur de type :"+type);
			throw new RuntimeException("Type not managed: "+type);
		}
		return o;
	}

	/*
	 * Convert a String to the good object
	 * @param type
	 * @param value
	 * @return
	 */
	public static Object convertStringToType(String type, String value){
		try {
			Class<?> valueClass = getClass(type);
			return convertValue(valueClass, value, null);
		} catch(Throwable e) {
			logger.error(e.getMessage(),e);			
		}
		return null;		
	}
	
	public static List<Object> convertStringToType(String type, List<String> values){
		try {
			Class<?> valueClass = getClass(type);
			List<Object> objects = new ArrayList<Object>(values.size());
			for (String value : values) {
				objects.add(convertValue(valueClass, value, null));
			}			
			return objects;
		} catch (Throwable e) { // TODO : do not catch throwable
			logger.error(e.getMessage(),e);			
		}
		return null;		
	}
	
	
	public static Object convertValue(Class<?> valueClass, Object value, String inputFormat) {
		if (Number.class.isAssignableFrom(value.getClass())) {
			return convertValue(valueClass, (Number)value);
		} else {
			return convertValue(valueClass, value.toString(), inputFormat);
		}
	}
	
	/*
	 * 
	 * @param type : final type
	 * @param value : the value
	 * @param format : format Date
	 * @return
	 */
	public static Object convertValue(Class<?> type, Number value) {
		Object o = null;
		if (String.class.equals(type)) {
			o = value.toString();
		} else if (Integer.class.equals(type)) {
			o = value.intValue();
		} else if (Double.class.equals(type)) {
			o = value.doubleValue();
		} else if (Float.class.equals(type)) {
			o = value.floatValue();
		} else if (Long.class.equals(type)) {
			o = value.longValue();
		} else if (Date.class.equals(type)) {
			o = new Date(value.longValue());			
		} else {
			logger.info("Erreur de type :"+type);
			throw new RuntimeException("Type not managed: "+type);
		}
		return o;
	}
	
	/*
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
					o = simpleDateFormat.parse(value);
				} catch (ParseException e) {
					throw new RuntimeException(e.getMessage(),e);
				}
			}
		} else if (TBoolean.class.equals(type)) {
			o = TBoolean.valueOf(value);
		} else {
			logger.info("Erreur de type :"+type);
			throw new RuntimeException("Type not managed: "+type);
		}
		return o;
	}
	
	/*
	 * Check if the property is not empty (null, "", " "; size = 0, etc.)
	 * @param errors
	 * @param object
	 * @param key
	 * @return
	 * @deprecated
	 */
	public static boolean required(Map<String, List<ValidationError>> errors, Object object, String key){
		boolean isValid = true;
		if (object == null) {
			isValid =  false;
        }
        if (isValid && object instanceof String) {
        	isValid =  StringUtils.isNotBlank((String)object);
        }
        if (isValid && object instanceof Collection) {
        	isValid =  CollectionUtils.isNotEmpty((Collection<?>)object);
        }
        if (isValid && object instanceof Map) {
        	isValid =  MapUtils.isNotEmpty((Map<?,?>)object);
        }
        if (!isValid) {
        	addErrors(errors, key, ERROR_REQUIRED_MSG,object);
        }        
        return isValid;		
	}
	
		
	/**
	 * add an error message
	 * @param errors    list of errors
	 * @param key       property key
	 * @param message   message key
	 * @param arguments message args
	 * @deprecated use ContextValidation.addErrors
	 */
	@Deprecated
	public static void addErrors(Map<String, List<ValidationError>> errors,	String key, String message, Object... arguments) {
		if (!errors.containsKey(key)) {
			errors.put(key, new ArrayList<ValidationError>());
		}		
		errors.get(key).add(new ValidationError(key, message,  java.util.Arrays.asList(arguments)));
	}

	/*
	 * 
	 * @param rootKeyName
	 * @param property
	 * @return
	 * @deprecated used ContextValidation.getKey
	 */
	public static String getKey(String rootKeyName, String property) {
		return (StringUtils.isBlank(rootKeyName))?property: rootKeyName+"."+property;
	}
	
	/*
	 * Transform the value of propertyValue to the good type
	 * @param propertyValue
	 * @param propertyDefinition
	 * @param contextValidation
	 * @return
	 */
	public static boolean convertPropertyValue(ContextValidation contextValidation, PropertySingleValue propertyValue, PropertyDefinition propertyDefinition) {
		try {
			propertyValue.value = cleanValue(propertyValue.value);
			Class<?> valueClass = getClass(propertyDefinition.valueType);
			if (propertyValue.value != null && !valueClass.isInstance(propertyValue.value)) { //transform only if not the good type
				//Logger.debug("convertValue "+propertyDefinition.code);
				propertyValue.value = convertValue(valueClass, propertyValue.value, null);
			}
			if (propertyDefinition.saveMeasureValue != null && propertyValue.unit == null) {
				propertyValue.unit = propertyDefinition.saveMeasureValue.value; 
			}
		} catch(Throwable e) {
			logger.error(e.getMessage(),e);
			contextValidation.addErrors(propertyDefinition.code+".value", ERROR_BADTYPE_MSG, propertyDefinition.valueType, propertyValue.value);
			return false;
		}
		return true;
	}
	
	public static Object cleanValue(Object object){
		if (object == null) {
			return null;
        } else if (object instanceof String && StringUtils.isBlank((String)object)) {
        	return null;
        } else if (object instanceof Collection && CollectionUtils.isEmpty((Collection<?>)object)) {
        	return null;        	
        } else if (object instanceof Map && MapUtils.isEmpty((Map<?,?>)object)) {
        	return null;       	
        } else if (object instanceof byte[] && ((byte[])object).length==0) {
        	return null;
        } else {
        	return object;
        }
	}
	
	/*
	 * Transform the value of propertyValue to the good type
	 * @param propertyValue
	 * @param propertyDefinition
	 * @param contextValidation
	 * @return
	 */
	public static boolean convertPropertyValue(ContextValidation contextValidation, PropertyListValue propertyValue, PropertyDefinition propertyDefinition) {
		try{
			Class<?> valueClass = getClass(propertyDefinition.valueType);
//			List<Object> newList = new ArrayList<Object>(propertyValue.value.size());
			List<Object> newList = new ArrayList<Object>(propertyValue.listValue().size());
//			for (Object value : propertyValue.value) {
			for (Object value : propertyValue.listValue()) {
				if (!valueClass.isInstance(value)) { //transform only if not the good type
					value = convertValue(valueClass, value, null);
				}
				newList.add(value);
			}			
			propertyValue.value = newList;
			if(propertyDefinition.saveMeasureValue!=null && propertyValue.unit == null){
				propertyValue.unit=propertyDefinition.saveMeasureValue.value; 
			}
		} catch(Throwable e) { // TODO: do not catch throwable
			logger.error(e.getMessage(),e);
			contextValidation.addErrors(propertyDefinition.code, ERROR_BADTYPE_MSG, propertyDefinition.valueType, propertyValue.value);
			return false;
		}
		return true;
	}
	
	
	/*
	 * Transform the value of propertyValue to the good type
	 * @param propertyValue
	 * @param propertyDefinition
	 * @param contextValidation
	 * @return
	 */
	public static boolean convertPropertyValue(ContextValidation contextValidation, PropertyObjectValue propertyValue, PropertyDefinition propertyDefinition) {
		String[] codes = splitCodePropertyDefinition(propertyDefinition);
		try {
			Class<?> valueClass = getClass(propertyDefinition.valueType);
//			Map<String, Object> map = (Map<String, Object>) propertyValue.value;
//			Map<String, Object> map = (Map<String, Object>) propertyValue.value;
			Map<String, Object> map = propertyValue.getValue();
//			Map<String, ?> map = propertyValue.value;
			Object value = map.get(codes[1]);
			if (!valueClass.isInstance(value) && value != null) { //transform only if not the good type
				value = convertValue(valueClass, value, null);
			}	
			map.put(codes[1], value);			
		} catch(Throwable e) {
			logger.error(e.getMessage(),e);
			contextValidation.addErrors(codes[0] + ".value."+codes[1], ERROR_BADTYPE_MSG, propertyDefinition.valueType, propertyValue.value);
			return false;
		}
		return true;
	}
	
	/*
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
//			List<Map<String, ?>> list = propertyValue.value;
//			List<Map<String, Object>> list = propertyValue.value;
			List<Map<String, Object>> list = propertyValue.listMapValue();
//			for(Map<String, ?> map: list){
			for (Map<String, Object> map : list) {
				Object value = map.get(codes[1]);
				if (!valueClass.isInstance(value) && value != null) { // transform only if not the good type
					value = convertValue(valueClass, value, null);
				}	
//				((Map<String, Object>)map).put(codes[1], value);
				map.put(codes[1], value);
			}
		} catch(Throwable e) {
			logger.error(e.getMessage(),e);
			contextValidation.addErrors(codes[0]+".value."+codes[1], ERROR_BADTYPE_MSG, propertyDefinition.valueType, propertyValue.value);
			return false;
		}
		return true;
	}
	
	/*
	 * Check if propertyValue is required
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return
	 */
//	public static boolean required(ContextValidation contextValidation, PropertySingleValue propertyValue, PropertyDefinition propertyDefinition){
//		if (propertyDefinition.required && required(contextValidation, propertyValue, propertyDefinition.code)) {
//			return required(contextValidation, propertyValue.value, propertyDefinition.code+".value");
////		}else if(propertyDefinition.required){
////			return false;
////		}else{
////			return true;
////		}
//		} else { 
//			return ! propertyDefinition.required;
//		}
//	}
	public static boolean required(ContextValidation contextValidation, PropertySingleValue propertyValue, PropertyDefinition propertyDefinition) {
		if (!propertyDefinition.required)
			return true;
		return required(contextValidation, propertyValue, propertyDefinition.code)
			&& required(contextValidation, propertyValue.value, propertyDefinition.code + ".value");
	}
	
	/*
	 * Check if propertyValue is required
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return
	 */
//	public static boolean required(ContextValidation contextValidation, PropertyListValue propertyValue, PropertyDefinition propertyDefinition){
//		if (propertyDefinition.required && required(contextValidation, propertyValue, propertyDefinition.code)) {
//			boolean isValid = true;
//			if (CollectionUtils.isNotEmpty(propertyValue.value)) {
//				int i = 0;
//				for (Object value : propertyValue.value) {
//					if(!required(contextValidation, value, propertyDefinition.code+".value["+i+++"]")){
//						isValid = false;
//					}
//				}	
//	        } else {
//	        	isValid = false;
//	        	contextValidation.addErrors(propertyDefinition.code+".value", ERROR_REQUIRED_MSG,propertyValue.value);
//	        }			
//			return isValid;
////		} else if(propertyDefinition.required) {
////			return false;
////		}else{
////			return true;
////		}
//		} else {
//			return ! propertyDefinition.required;
//		}
//	}
	
	public static boolean required(ContextValidation contextValidation, PropertyListValue propertyValue, PropertyDefinition propertyDefinition) {
		if (!propertyDefinition.required)
				return true;
		if (!required(contextValidation, propertyValue, propertyDefinition.code))
			return false;
		boolean isValid = true;
//		if (CollectionUtils.isNotEmpty(propertyValue.value)) {
		if (CollectionUtils.isNotEmpty(propertyValue.listValue())) {
			int i = 0;
//			for (Object value : propertyValue.value) {
			for (Object value : propertyValue.listValue()) {
				if (!required(contextValidation, value, propertyDefinition.code + ".value[" + i++ + "]")) {
					isValid = false;
				}
			}	
		} else {
			isValid = false;
			contextValidation.addErrors(propertyDefinition.code+".value", ERROR_REQUIRED_MSG,propertyValue.value);
		}			
		return isValid;
	}
	
	
	/*
	 * Check if propertyValue is required
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return
	 */
	public static boolean required(ContextValidation contextValidation, PropertyObjectValue propertyValue, PropertyDefinition propertyDefinition){
		if (propertyDefinition.required && required(contextValidation, propertyValue, propertyDefinition.code)) {		
			String[] codes = splitCodePropertyDefinition(propertyDefinition);
//			Map<String, Object> map = (Map<String, Object>) propertyValue.value;
//			Object value = map.get(codes[1]);
//			Object value = propertyValue.value.get(codes[1]);
			Object value = propertyValue.mapValue().get(codes[1]);
			return required(contextValidation, value, codes[0]+".value."+codes[1]);
		} else if(propertyDefinition.required) {
			return false;
		} else {
			return true;
		}
	}
	
	/*
	 * Check if propertyValue is required
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return
	 */
//	public static boolean required(ContextValidation contextValidation, PropertyObjectListValue propertyValue, PropertyDefinition propertyDefinition){
//		if (propertyDefinition.required && required(contextValidation, propertyValue, propertyDefinition.code)) {
//			String[] codes = splitCodePropertyDefinition(propertyDefinition);
//			List<Map<String, ?>> list = (List<Map<String, ?>>) propertyValue.value;
//			List<Map<String, Object>> list = (List<Map<String, ?>>) propertyValue.value;
//			int i = 0;
//			boolean isValid = true;
//			for(Map<String, ?> map: list){
//				Object value = map.get(codes[1]);
//				if(!required(contextValidation, value, codes[0]+".value["+i+++"]."+codes[1])){
//					isValid = false;
//				}
//			}		
//			return isValid;
//		} else if (propertyDefinition.required) {
//			return false;
//		} else {
//			return true;
//		}
//	}
	
	public static boolean required(ContextValidation contextValidation, PropertyObjectListValue propertyValue, PropertyDefinition propertyDefinition){
		if (propertyDefinition.required && required(contextValidation, propertyValue, propertyDefinition.code)) {
			String[] codes = splitCodePropertyDefinition(propertyDefinition);
			int i = 0;
			boolean isValid = true;
//			for (Map<String, Object> map : propertyValue.value){
			for (Map<String, Object> map : propertyValue.listMapValue()) {
				Object value = map.get(codes[1]);
				if (!required(contextValidation, value, codes[0]+".value["+i+++"]."+codes[1])) {
					isValid = false;
				}
			}		
			return isValid;
//		} else if (propertyDefinition.required) {
//			return false;
//		} else {
//			return true;
//		}
		} else {
			return ! propertyDefinition.required;
		}
	}
	
	/*
	 * 
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return
	 */
	public static boolean required(ContextValidation contextValidation, PropertyByteValue propertyValue, PropertyDefinition propertyDefinition){
		if (propertyDefinition.required && required(contextValidation, propertyValue, propertyDefinition.code)) {
			return required(contextValidation, propertyValue.value, propertyDefinition.code+".value");
//		} else if(propertyDefinition.required) {
//			return false;
//		}else{
//			return true;
//		}
		} else {
			return ! propertyDefinition.required;
		}
	}
	
	/*
	 * Split the propertyDefinition code with "." if exist
	 * @param propertyDefinition
	 * @return
	 */
	private static String[] splitCodePropertyDefinition(PropertyDefinition propertyDefinition) {
		return propertyDefinition.code.split("\\.", 2);
	}
	
	/*
	 * Check if the property is not empty (null, "", " "; size = 0, etc.)
	 * @param object
	 * @param key
	 * @return
	 */
	public static boolean required(ContextValidation contextValidation, Object object, String property) {
		boolean isValid = true;
		if (object == null) {
			isValid = false;
        }
        if (isValid && object instanceof String) {
        	isValid =  StringUtils.isNotBlank((String)object);
        }
        
        if (isValid && object instanceof Collection) {
        	isValid =  CollectionUtils.isNotEmpty((Collection<?>)object);        	
        }
        
        if (isValid && object instanceof Map) {
        	isValid =  MapUtils.isNotEmpty((Map<?,?>)object);        	
        }
        
        if (isValid && object instanceof byte[]) {
        	byte[] byteArrayObject =  (byte[]) object;
        	isValid = (byteArrayObject.length==0?false:true);
        }
        if (!isValid) {
        	contextValidation.addErrors(property, ERROR_REQUIRED_MSG,object);
        }        
        return isValid;		
	}
	
	// Assertion style, cases are mutually exclusive
	public static boolean required_(ContextValidation contextValidation, Object object, String property) {
		if (object == null) {
			contextValidation.addErrors(property, ERROR_REQUIRED_MSG, object);
			return false;
		}
        if ((object instanceof String) && StringUtils.isNotBlank((String)object))
        	return true;
        if ((object instanceof Collection) && CollectionUtils.isNotEmpty((Collection<?>)object))        	
        	return true;
        if ((object instanceof Map) && MapUtils.isNotEmpty((Map<?,?>)object))        	
        	return true;
        if ((object instanceof byte[]) && (((byte[])object).length > 0))
        	return true;        
        contextValidation.addErrors(property, ERROR_REQUIRED_MSG,object);        
        return false;		
	}
	
	/*
	 * Check if the value is in the list
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return
	 */
	public static boolean checkIfExistInTheList(ContextValidation contextValidation, PropertySingleValue propertyValue, PropertyDefinition propertyDefinition){
		if(propertyDefinition.choiceInList && !checkIfExistInTheList(propertyDefinition, propertyValue.value)){
			contextValidation.addErrors(propertyDefinition.code+".value", ERROR_VALUENOTAUTHORIZED_MSG, propertyValue.value);
			return false;
		}else{
			return true;
		}
	}
	
	/*
	 * Check if the value is in the list
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return
	 */
	public static boolean checkIfExistInTheList(ContextValidation contextValidation, PropertyListValue propertyValue, PropertyDefinition propertyDefinition){
		if (propertyDefinition.choiceInList) {
			int i = 0;
			boolean isOk = true;
//			for(Object value : propertyValue.value){
			for (Object value : propertyValue.listValue()) {
				if(!checkIfExistInTheList(propertyDefinition, value)){
					contextValidation.addErrors(propertyDefinition.code+".value["+i+++"]", ERROR_VALUENOTAUTHORIZED_MSG, value);
					isOk = false;
				}
			}
			return isOk;
		} else {
			return true;
		}
	}
	
	
	/*
	 * 
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 */
	public static boolean checkIfExistInTheList(ContextValidation contextValidation, PropertyObjectValue propertyValue, PropertyDefinition propertyDefinition) {
		if (propertyDefinition.choiceInList) {
			boolean isOk = true;
//			for (Entry<String, ?> entryValue : propertyValue.value.entrySet()){
			for (Entry<String, ?> entryValue : propertyValue.mapValue().entrySet()){
				Object value = entryValue.getValue();
				if((propertyDefinition.code.endsWith(entryValue.getKey())) && !checkIfExistInTheList(propertyDefinition, value)){
					contextValidation.addErrors(propertyDefinition.code+".value."+entryValue.getKey(), ERROR_VALUENOTAUTHORIZED_MSG, value);
					isOk = false;
				}
			}			
			return isOk;
		} else {
			return true;
		}
	}
	
	/*
	 * 
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinition
	 * @return 
	 */
	public static boolean checkIfExistInTheList(ContextValidation contextValidation, PropertyObjectListValue propertyValue, PropertyDefinition propertyDefinition) {
		if (propertyDefinition.choiceInList) {
			int i = 0;
			boolean isOk = true;
//			for(Map<String, ?> map : propertyValue.value){
			for (Map<String, ?> map : propertyValue.listMapValue()) {
				for (Entry<String, ?> entryValue : map.entrySet()) {
					Object value = entryValue.getValue();
					if ((propertyDefinition.code.endsWith(entryValue.getKey())) && !checkIfExistInTheList(propertyDefinition, value)) {
						contextValidation.addErrors(propertyDefinition.code+".value["+i+++"]."+entryValue.getKey(), ERROR_VALUENOTAUTHORIZED_MSG, value);
						isOk = false;
					}
				}								
			}
			return isOk;	
		} else {
			return true;
		}
	}
	
	/*
	 * 
	 * @param propertyDefinition
	 * @param value
	 * @return
	 */
	public static boolean checkIfExistInTheList(
			PropertyDefinition propertyDefinition, Object value) {
		Class<?> valueClass = getClass(propertyDefinition.valueType);
		for(Value possibleValue : propertyDefinition.possibleValues){
			if(value.equals(convertValue(valueClass, possibleValue.code, null))){
				return true;
			}			
		}
		return false;
	}
	
	/*
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

	
	/*
	 * Check if property value type is the same as property definition type 
	 * @param contextValidation
	 * @param propertyValue
	 * @param propertyDefinitions
	 * @return
	 */
//	public static boolean checkType(ContextValidation contextValidation, PropertyValue<?> propertyValue, Collection<PropertyDefinition> propertyDefinitions) {
//		boolean isSame = true;
//		for (PropertyDefinition propDef : propertyDefinitions) {
//			if (propertyValue._type == null || !propertyValue._type.equals(propDef.propertyValueType)) {
//				logger.error("Error property "+propDef.code+" : "+propertyValue.value+" expected "+propDef.propertyValueType+ " found "+propertyValue._type);
//				//TODO à activer si la prod se passe bien
//				//contextValidation.addErrors(propDef.code, ERROR_PROPERTY_TYPE, propertyValue.value, propDef.propertyValueType,propertyValue._type);
//				isSame = false;
//			}
//		}
//		return isSame;
//	}
	public static boolean checkType(ContextValidation contextValidation, PropertyValue propertyValue, Collection<PropertyDefinition> propertyDefinitions) {
		boolean isSame = true;
		for (PropertyDefinition propDef : propertyDefinitions) {
			if (propertyValue._type == null || !propertyValue._type.equals(propDef.propertyValueType)) {
//				logger.error("Error property "+propDef.code+" : "+propertyValue.value+" expected "+propDef.propertyValueType+ " found "+propertyValue._type);
				logger.error("Error property {} : {}, expected {} got {}", propDef.code, propertyValue.value, propDef.propertyValueType, propertyValue._type);
				//TODO à activer si la prod se passe bien
				//contextValidation.addErrors(propDef.code, ERROR_PROPERTY_TYPE, propertyValue.value, propDef.propertyValueType,propertyValue._type);
				isSame = false;
			}
		}
		return isSame;
	}
	
}
