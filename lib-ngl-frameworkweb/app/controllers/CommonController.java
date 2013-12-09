package controllers;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.vz.mongodb.jackson.DBQuery;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;

import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.With;

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
	public static <T extends DBObject> MongoDBResult<T> mongoDBFinder(String collection, ListForm form, Class<T> type, DBQuery.Query query){
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

	public static <T extends DBObject> MongoDBResult<T> mongoDBFinder(String collection, ListForm form, Class<T> type, DBQuery.Query query, BasicDBObject keys){
		MongoDBResult<T> results = null;
		if(form.datatable){
			results = MongoDBDAO.find(collection, type, query, keys) 
					.sort(form.orderBy, Sort.valueOf(form.orderSense))
					.page(form.pageNumber,form.numberRecordsPerPage); 
		}else{
			results = MongoDBDAO.find(collection, type, query) 
					.sort(form.orderBy, Sort.valueOf(form.orderSense))
					.limit(form.limit);
		}
		return results;
	}

}
