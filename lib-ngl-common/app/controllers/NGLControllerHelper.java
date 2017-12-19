package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
//import java.util.stream.Collector;
import java.util.stream.Collectors;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.utils.dao.DAOException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

// import play.Logger;
import validation.utils.ValidationHelper;

public class NGLControllerHelper {
	
	public static Query generateQueriesForProperties(Map<String, List<String>> properties,
			Level.CODE level, 
			List<String> prefixPropertyPath) {
		
		List<Query> queryElts = prefixPropertyPath.stream()
				.map(prefix -> generateQueriesForProperties(properties, level, prefix))
				.flatMap(List::stream)
				.collect(Collectors.toList());
		
		return DBQuery.or(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
	}
	
	public static List<Query> generateQueriesForProperties(Map<String, List<String>> properties,
			Level.CODE level, 
			String prefixPropertyPath) {
		List<Query> queries = new ArrayList<Query>();
		try {
			for(String keyValue : properties.keySet()){
				
				String[] key = keyValue.split("\\|",2);
				
				PropertyDefinition pd = PropertyDefinition.find.findUnique(key[0], level);
				List<String> stringValues = properties.get(keyValue);
				if(null != pd && CollectionUtils.isNotEmpty(stringValues)){					
					Query subQueries = DBQuery.empty();
					if(key.length == 1){
						List<Object> values = ValidationHelper.convertStringToType(pd.valueType, stringValues);
					
						//use $in because is more generic than $is and work to field of type array or single
						subQueries = DBQuery.in(prefixPropertyPath+"."+key[0]+".value", values);
						//in case of property is not defined in the document ???
						if(Boolean.class.getName().equals(pd.valueType) && !((Boolean)values.get(0)).booleanValue()){
							subQueries = DBQuery.or(subQueries, DBQuery.notExists(prefixPropertyPath+"."+key[0]+".value"));
						}						
					}else if(key.length > 1 && stringValues.size() == 1){
						if(key[1].equals("regex")){
							Pattern pattern = convertStringToPattern(stringValues.get(0));
							subQueries = DBQuery.regex(prefixPropertyPath+"."+key[0]+".value", pattern);
							queries.add(subQueries);							
						}else if(key[1].equals("gte") && stringValues.size() == 1){
							Object value = ValidationHelper.convertStringToType(pd.valueType, stringValues.get(0));
							subQueries = DBQuery.greaterThanEquals(prefixPropertyPath+"."+key[0]+".value", value);							
						}else if(key[1].equals("gt") && stringValues.size() == 1){
							Object value = ValidationHelper.convertStringToType(pd.valueType, stringValues.get(0));
							subQueries = DBQuery.greaterThan(prefixPropertyPath+"."+key[0]+".value", value);							
						}else if(key[1].equals("lte") && stringValues.size() == 1){
							Object value = ValidationHelper.convertStringToType(pd.valueType, stringValues.get(0));
							subQueries = DBQuery.lessThanEquals(prefixPropertyPath+"."+key[0]+".value", value);							
						}else if(key[1].equals("lt") && stringValues.size() == 1){
							Object value = ValidationHelper.convertStringToType(pd.valueType, stringValues.get(0));
							subQueries = DBQuery.lessThan(prefixPropertyPath+"."+key[0]+".value", value);							
						}else if(key[1].equals("exists") && stringValues.size() == 1){
							if("TRUE".equals(stringValues.get(0).toUpperCase())){
								subQueries = DBQuery.exists(prefixPropertyPath+"."+key[0]+".value");
							}else if("FALSE".equals(stringValues.get(0).toUpperCase())){
								subQueries = DBQuery.notExists(prefixPropertyPath+"."+key[0]+".value");
							}
						}else{
							throw new RuntimeException("key[1] not valid : "+key[1]);
						}
					}else{
						throw new RuntimeException("key not valid : "+keyValue+" or stringValues.size != 1 / "+stringValues.size());
					}
					queries.add(subQueries);
				}				
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
		return queries;
	}
	
	private static Pattern convertStringToPattern(String value) {
		
		return Pattern.compile(value);
	}
	
	private static List<Pattern> convertStringToPatterns(List<String> values) {
		List<Pattern> objects = new ArrayList<Pattern>(values.size());
		for(String value : values){
			objects.add(Pattern.compile(value));
		}	
		return objects;
	}

	public static List<Query> generateQueriesForTreatmentProperties(Map<String, Map<String, List<String>>> treatmentProperties, Level.CODE level, 
			String prefixPropertyPath) {
		List<Query> queries = new ArrayList<Query>();
		for(String key : treatmentProperties.keySet()){
			queries.addAll(generateQueriesForProperties(treatmentProperties.get(key), level, prefixPropertyPath+"."+key));			
		}
		return queries;
	}

	public static Collection<? extends Query> generateQueriesForExistingProperties(Map<String, Boolean> existingFields) {
		List<Query> queries = new ArrayList<Query>();
		if (MapUtils.isNotEmpty(existingFields)) { //all
			for(String field : existingFields.keySet()){
				if(Boolean.FALSE.equals(existingFields.get(field))){
					queries.add(DBQuery.notExists(field));
				}else if(Boolean.TRUE.equals(existingFields.get(field))){
					queries.add(DBQuery.exists(field));
				}
			}		
		}
		return queries;
	}
}
