package controllers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate.Builder;

import org.apache.commons.lang3.StringUtils;
import org.bson.BSONObject;
import org.codehaus.jackson.JsonNode;

import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.With;
import validation.ContextValidation;
import views.components.datatable.DatatableForm;

import com.mongodb.BasicDBObject;

import controllers.history.UserHistory;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.MongoDBResult.Sort;


@With({fr.cea.ig.authentication.Authenticate.class, UserHistory.class})
public abstract class CommonController extends Controller{

	protected final static DynamicForm listForm = new DynamicForm();


	/**
	 * Fill a form in json mode
	 * @param form
	 * @param clazz
	 * @return
	 */
	protected static <T> Form<T> getFilledForm(Form<T> form, Class<T> clazz) {		
		JsonNode json = request().body().asJson();
		T input = Json.fromJson(json, clazz);
		Form<T> filledForm = form.fill(input); 
		return filledForm;
	}
	
	protected static <T> List<Form<T>> getFilledFormList(Form<T> form, Class<T> clazz) {		
		JsonNode json = request().body().asJson();
		List<Form<T>> results = new ArrayList<Form<T>>();
		Iterator<JsonNode> iterator = json.getElements();
		
		while(iterator.hasNext()){
			JsonNode jsonChild = iterator.next();
			T input = Json.fromJson(jsonChild, clazz);
			Form<T> filledForm = form.fill(input);
			results.add(filledForm);
		}
		
		return results;
	}

	/**
	 * Fill a form in json mode
	 * @param form
	 * @param clazz
	 * @return
	 */
	protected static <T> Form<T> filledFormQueryString(Form<T> form, Class<T> clazz) {		
		Map<String, String[]> queryString =request().queryString();
		Map<String, Object> transformMap = new HashMap<String, Object>();
		for(String key :queryString.keySet()){			
			try {
				if(isNotEmpty(queryString.get(key))){				
					Field field = clazz.getField(key);
					Class type = field.getType();
					if(type.isArray() || Collection.class.isAssignableFrom(type)){
						transformMap.put(key, queryString.get(key));						
					}else{
						transformMap.put(key, queryString.get(key)[0]);						
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 

		}

		JsonNode json = Json.toJson(transformMap);
		T input = Json.fromJson(json, clazz);
		Form<T> filledForm = form.fill(input); 
		return filledForm;
	}


	private static boolean isNotEmpty(String[] strings) {
		if(null == strings)return false;
		if(strings.length == 0)return false;
		if(strings.length == 1 && StringUtils.isBlank(strings[0]))return false;
		return true;
	}

	public static String getCurrentUser(){
		return Context.current().request().username();
	}
	
	/**
	 * A finder for mongoDB
	 * @param collection
	 * @param form
	 * @param type (DBObject)
	 * @param query
	 * @return a MongoDBResult
	 */
	protected static <T extends DBObject> MongoDBResult<T> mongoDBFinder(String collection, ListForm form, Class<T> type, DBQuery.Query query){
		MongoDBResult<T> results = null;
		if(form.datatable){
			results = MongoDBDAO.find(collection, type, query) 
					.sort(form.orderBy, Sort.valueOf(form.orderSense))
					.page(form.pageNumber,form.numberRecordsPerPage); 
		}else{
			results = MongoDBDAO.find(collection, type, query) 
					.sort(form.orderBy, Sort.valueOf(form.orderSense))
					.limit(form.limit);
		}
		return results;
	}

	protected static <T extends DBObject> MongoDBResult<T> mongoDBFinder(String collection, ListForm form, Class<T> type, DBQuery.Query query, BasicDBObject keys){
		MongoDBResult<T> results = null;
		if(form.datatable){
			results = MongoDBDAO.find(collection, type, query, keys) 
					.sort(form.orderBy, Sort.valueOf(form.orderSense))
					.page(form.pageNumber,form.numberRecordsPerPage); 
		}else{
			results = MongoDBDAO.find(collection, type, query, keys) 
					.sort(form.orderBy, Sort.valueOf(form.orderSense))
					.limit(form.limit);
		}
		return results;
	}
	
	protected static BasicDBObject getKeys(DatatableForm form) {
		BasicDBObject keys = new BasicDBObject();
		keys.putAll((BSONObject)getIncludeKeys(form.includes.toArray(new String[form.includes.size()])));
		keys.putAll((BSONObject)getExcludeKeys(form.excludes.toArray(new String[form.excludes.size()])));		
		return keys;
	}
	
	protected static BasicDBObject getIncludeKeys(String[] keys) {
		BasicDBObject values = new BasicDBObject();
		for(String key : keys){
		    values.put(key, 1);
		}
		return values;
    }
	
	protected static BasicDBObject getExcludeKeys(String[] keys) {
		BasicDBObject values = new BasicDBObject();
		for(String key : keys){
		    values.put(key, 0);
		}
		return values;
    }
	
	/**
	 * Construct a builder from some fields
	 * Use to update a mongodb document
	 * @param value
	 * @param fields
	 * @param clazz
	 * @return
	 */
	protected static Builder getBuilder(Object value, List<String> fields, Class clazz) {
		Builder builder = new Builder();
		try {
			for(String field: fields){
				builder.set(field, clazz.getDeclaredField(field).get(value));
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
		return builder;
	}
	/**
	 * Validate authorized field for specific update field
	 * @param ctxVal
	 * @param fields
	 * @param authorizedUpdateFields
	 */
	protected static void validateAuthorizedUpdateFields(ContextValidation ctxVal, List<String> fields,
			List<String> authorizedUpdateFields) {
		for(String field: fields){
			if(!authorizedUpdateFields.contains(field)){
				ctxVal.addErrors("fields", "error.valuenotauthorized", field);
			}
		}				
	}
	
	/**
	 * Validate ig the field is present in the form
	 * @param ctxVal
	 * @param fields
	 * @param filledForm
	 */
	protected static void validateIfFieldsArePresentInForm(
			ContextValidation ctxVal, List<String> fields, Form<?> filledForm) {
		for(String field: fields){
			if(filledForm.field(field).value() == null){
				ctxVal.addErrors(field, "error.notdefined");
			}
		}	
		
	}
}
