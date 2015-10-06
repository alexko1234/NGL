package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.utils.dao.DAOException;

import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.Logger;
import validation.utils.ValidationHelper;

public class NGLControllerHelper {
	
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
					Query subQueries = null;
					List<Object> values = null;
					if(key.length == 1){
						values = ValidationHelper.convertStringToType(pd.valueType, stringValues);
					}else if(key.length > 1){
						if(key[1].equals("regex")){
							values = convertStringToPattern(stringValues);
						}else{
							throw new RuntimeException("key[1] not valid : "+key[1]);
						}
					}else{
						throw new RuntimeException("key not valid : "+keyValue);
					}
					
					//use $in because is more generic than $is and work to field of type array or single
					subQueries = DBQuery.in(prefixPropertyPath+"."+key[0]+".value", values);
					//in case of property is not defined in the document ???
					if(Boolean.class.getName().equals(pd.valueType) && !((Boolean)values.get(0)).booleanValue()){
						subQueries = DBQuery.or(subQueries, DBQuery.notExists(prefixPropertyPath+"."+key[0]+".value"));
					}
					queries.add(subQueries);
				}				
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
		return queries;
	}
	
	private static List<Object> convertStringToPattern(List<String> values) {
		List<Object> objects = new ArrayList<Object>(values.size());
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
}
