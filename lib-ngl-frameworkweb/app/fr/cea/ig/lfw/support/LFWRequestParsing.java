package fr.cea.ig.lfw.support;

import static play.mvc.Http.Context.Implicit.request;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
// import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import fr.cea.ig.lfw.LFWApplicationHolder;
// import play.data.Form;
import play.libs.Json;

/**
 * HTTP request parsing. 
 * thing.
 * 
 * @author vrd
 *
 */
public interface LFWRequestParsing extends LFWApplicationHolder {

	// This is extracted from common controller
	
	default <T> T objectFromRequestQueryString(Class<T> clazz) {
		Map<String, String[]> queryString = request().queryString();
		Map<String, Object> transformMap = new HashMap<String, Object>();
		for (String key : queryString.keySet()) {
			try {
				if (isNotEmpty(queryString.get(key))) {				
					Field field = clazz.getField(key);
					Class<?> type = field.getType();
					if (type.isArray() || Collection.class.isAssignableFrom(type)) {
						transformMap.put(key, queryString.get(key));						
					} else {
						transformMap.put(key, queryString.get(key)[0]);						
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
		}
		return Json.fromJson(Json.toJson(transformMap),clazz);
	}
	
	default <T> T objectFromRequestBody(Class<T> clazz) {
		return Json.fromJson(request().body().asJson(),clazz);
	}
	
	default <T> List<T> objectListFromRequestBody(Class<T> clazz) {		
		JsonNode json = request().body().asJson();
		List<T> results = new ArrayList<T>();
		json.forEach( n -> results.add(Json.fromJson(n, clazz)));
		return results;
	}

	default boolean isNotEmpty(String[] strings) {
		if (strings == null)     return false;
		if (strings.length == 0) return false;
		if (strings.length == 1 && StringUtils.isBlank(strings[0])) return false;
		return true;
	}

}
