package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.utils.dao.DAOException;

import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import validation.utils.ValidationHelper;

public class NGLControllerHelper {
	
	public static List<Query> generateQueriesForProperties(Map<String, List<String>> properties,
			Level.CODE level, 
			String prefixPropertyPath) {
		List<Query> queries = new ArrayList<Query>();
		try {
			for(String key : properties.keySet()){
				PropertyDefinition pd = PropertyDefinition.find.findUnique(key, level);
				List<String> stringValues = properties.get(key);
				if(null != pd && CollectionUtils.isNotEmpty(stringValues)){
					Query subQueries = null;
					List<Object> values = ValidationHelper.convertStringToType(pd.valueType, stringValues);
					//use $in because is more generic than $is and work to field of type array or single
					subQueries = DBQuery.in(prefixPropertyPath+"."+key+".value", values);
					//in case of property is not defined in the document ???
					if(Boolean.class.getName().equals(pd.valueType) && !((Boolean)values.get(0)).booleanValue()){
						subQueries = DBQuery.or(subQueries, DBQuery.notExists(prefixPropertyPath+"."+key+".value"));
					}
					queries.add(subQueries);
				}				
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
		return queries;
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
