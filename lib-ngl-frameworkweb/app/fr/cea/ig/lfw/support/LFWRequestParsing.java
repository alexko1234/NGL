package fr.cea.ig.lfw.support;

import static play.mvc.Http.Context.Implicit.request;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
// import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.BSONObject;
import org.terracotta.statistics.archive.SampleSink;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.BasicDBObject;

import fr.cea.ig.lfw.LFWApplicationHolder;
// import play.data.Form;
import play.libs.Json;
import views.components.datatable.IDatatableForm;

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
	
	default BasicDBObject getKeys(IDatatableForm form) {
		BasicDBObject keys = new BasicDBObject();
		if(!form.includes().contains("*")){
			keys.putAll((BSONObject)getIncludeKeys(form.includes().toArray(new String[form.includes().size()])));
		}
		keys.putAll((BSONObject)getExcludeKeys(form.excludes().toArray(new String[form.excludes().size()])));		
		return keys;
	}
	
	default BasicDBObject getIncludeKeys(String[] keys) {
		Arrays.sort(keys, Collections.reverseOrder());
		BasicDBObject values = new BasicDBObject();
		for(String key : keys){
		    values.put(key, 1);
		}
		return values;
    }
	
	default BasicDBObject getExcludeKeys(String[] keys) {
		Arrays.sort(keys, Collections.reverseOrder());
		BasicDBObject values = new BasicDBObject();
		for(String key : keys){
		    values.put(key, 0);
		}
		return values;
    }
	
	/**
	 * can not access to default keys in controller (restriction to the API)
	 * replace by: <br>
	 * if {@link IDatatableForm#includes()} contains "default" then call method of API with use default keys <br>
	 * else retrieve keys from form using {@link LFWRequestParsing#getKeys(IDatatableForm)}
	 * <br> 
	 * See example in: {@link controllers.samples.api.Samples#list()} 
	 * @param form IDatatableForm
	 * @param defaultKeys List<String>
	 * @return form IDatatableForm
	 */
	@Deprecated
	default IDatatableForm updateForm(IDatatableForm form, List<String> defaultKeys) {
		if(form.includes().contains("default")){
			form.includes().remove("default");
			if(defaultKeys != null){
				form.includes().addAll(defaultKeys);
			}
		}
		return form;
	}
}
