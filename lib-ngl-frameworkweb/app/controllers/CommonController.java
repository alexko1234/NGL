package controllers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.BSONObject;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate.Builder;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
//import org.springframework.core.convert.TypeDescriptor;

//import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.modules.jongo.MongoDBPlugin;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.routing.JavaScriptReverseRouter;
//import play.mvc.Results.StringChunks;
//import play.mvc.Results.Chunks.Out;
import play.mvc.Result;
import play.mvc.With;
// import scala.io.Source;
import validation.ContextValidation;
import views.components.datatable.DatatableForm;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.BasicDBObject;

//import akka.stream.javadsl.Source;
//import akka.util.ByteString;
import controllers.history.UserHistory;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.MongoDBResult.Sort;

import fr.cea.ig.mongo.MongoStreamer;

// New version is probably MongoCommonController<T>

// TODO: suggest a fix
// @Deprecated
@With({fr.cea.ig.authentication.Authenticate.class, UserHistory.class})
public abstract class CommonController extends Controller {
// abstract class UNUSED_CommonController extends Controller {

	// TODO: fix initialization
	//protected final static DynamicForm listForm = // new DynamicForm(null,null,null); // new DynamicForm()
		//	fr.cea.ig.play.IGGlobals.form();

	private static DynamicForm _listForm;
	
	protected final static DynamicForm listForm() {
		if (_listForm == null)
			_listForm = fr.cea.ig.play.IGGlobals.form();
		return _listForm;
	}
	
	/*
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
		Iterator<JsonNode> iterator = json.elements();
		
		while(iterator.hasNext()){
			JsonNode jsonChild = iterator.next();
			T input = Json.fromJson(jsonChild, clazz);
			Form<T> filledForm = form.fill(input);
			results.add(filledForm);
		}
		
		return results;
	}

	/*
	 * Fill a form in json mode
	 * @param form
	 * @param clazz
	 * @return
	 */
	protected static <T> Form<T> filledFormQueryString(Form<T> form, Class<T> clazz) {		
		Map<String, String[]> queryString = request().queryString();
		Map<String, Object> transformMap = new HashMap<String, Object>();
		for (String key :queryString.keySet()) {
			try {
				if(isNotEmpty(queryString.get(key))){				
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
		JsonNode json = Json.toJson(transformMap);
		T input = Json.fromJson(json, clazz);
		Form<T> filledForm = form.fill(input); 
		return filledForm;
	}

	/*
	 * Fill a form in json mode
	 * @param form
	 * @param clazz
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("unchecked")
	protected static <T> T filledFormQueryString(Class<T> clazz) {		
		try {
			Map<String, String[]> queryString = request().queryString();
			BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(clazz.newInstance());
			wrapper.setAutoGrowNestedPaths(true);
			for(String key :queryString.keySet()){
				
				try {
					if (isNotEmpty(queryString.get(key))) {
						Object value = queryString.get(key);
						if (wrapper.isWritableProperty(key)) {
							Class<?> c = wrapper.getPropertyType(key);
							//TODO used conversion spring system
							if (c != null && Date.class.isAssignableFrom(c)) {
								//wrapper.setPropertyValue(key, new Date(Long.valueOf(value[0])));
								value = new Date(Long.valueOf(((String[])value)[0]));
							}							
						}
						wrapper.setPropertyValue(key, value);
						
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				} 
	
			}
			return (T)wrapper.getWrappedInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	private static boolean isNotEmpty(String[] strings) {
		if (strings == null) return false;
		if (strings.length == 0) return false;
		if (strings.length == 1 && StringUtils.isBlank(strings[0])) return false;
		return true;
	}

	protected static String getCurrentUser(){
		//return Context.current().request().username();
		// return fr.cea.ig.authentication.Helper.username(Context.current().request());
		// return fr.cea.ig.authentication.Helper.username(Context.current().session());
		return fr.cea.ig.authentication.Authentication.getUser(Context.current().session());
	}
	
	/*
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
					.sort(form.orderBy, Sort.valueOf(form.orderSense));
			if(form.isServerPagination()){
				results.page(form.pageNumber,form.numberRecordsPerPage); 
			}
					
		}else{
			results = MongoDBDAO.find(collection, type, query) 
					.sort(form.orderBy, Sort.valueOf(form.orderSense));
			if(form.limit != -1){
				results.limit(form.limit);
			}
		}
		
		return results;
	}

	protected static <T extends DBObject> MongoDBResult<T> mongoDBFinder(String collection, ListForm form, Class<T> type, DBQuery.Query query, BasicDBObject keys){
		MongoDBResult<T> results = null;
		if(form.datatable){
			results = MongoDBDAO.find(collection, type, query, keys) 
					.sort(form.orderBy, Sort.valueOf(form.orderSense));
			if(form.isServerPagination()){
				results.page(form.pageNumber,form.numberRecordsPerPage); 
			}
		}else{
			results = MongoDBDAO.find(collection, type, query, keys) 
					.sort(form.orderBy, Sort.valueOf(form.orderSense));
			if(form.limit != -1){
				results.limit(form.limit);
			}
		}
		
		
		return results;
	}
	
	protected static BasicDBObject getKeys(DatatableForm form) {
		BasicDBObject keys = new BasicDBObject();
		if(null != form.includes && form.includes.size() > 0 && !form.includes.contains("*")){
			keys.putAll((BSONObject)getIncludeKeys(form.includes.toArray(new String[form.includes.size()])));			
		}else if(null != form.excludes && form.excludes.size() > 0){
			keys.putAll((BSONObject)getExcludeKeys(form.excludes.toArray(new String[form.excludes.size()])));					
		}
		return keys;
	}
	
	protected static BasicDBObject getIncludeKeys(String[] keys) {
		Arrays.sort(keys, Collections.reverseOrder());
		BasicDBObject values = new BasicDBObject();
		for(String key : keys){
		    values.put(key, 1);
		}
		return values;
    }
	
	protected static BasicDBObject getExcludeKeys(String[] keys) {
		Arrays.sort(keys, Collections.reverseOrder());
		BasicDBObject values = new BasicDBObject();
		for(String key : keys){
		    values.put(key, 0);
		}
		return values;
    }
	
	/*
	 * Construct a builder from some fields
	 * Use to update a mongodb document
	 * @param value
	 * @param fields
	 * @param clazz
	 * @return
	 */
	protected static Builder getBuilder(Object value, List<String> fields, Class<?> clazz) {
		return getBuilder(value, fields, clazz, null);
	}
	
	/*
	 * Construct a builder from some fields
	 * Use to update a mongodb document
	 * @param value
	 * @param fields
	 * @param clazz
	 * @return
	 */
	protected static Builder getBuilder(Object value, List<String> fields, Class<?> clazz, String prefix) {
		Builder builder = new Builder();
		try {
			for(String field: fields){
				String fieldName = (null != prefix)?prefix+"."+field:field;
				builder.set(fieldName, clazz.getField(field).get(value));
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
		return builder;
	}
	
	/*
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
	
	/*
	 * Validate ig the field is present in the form
	 * @param ctxVal
	 * @param fields
	 * @param filledForm
	 */
	protected static void validateIfFieldsArePresentInForm(ContextValidation ctxVal, List<String> fields, Form<?> filledForm) {
		for(String field: fields){
			if (filledForm.field(field).value() == null) {
				ctxVal.addErrors(field, "error.notdefined");
			}
		}	
	}
	
	protected static Calendar getToDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal;
	}

	protected static Calendar getFromDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal;
	}
	
	protected static <T extends DBObject> Result nativeMongoDBQQuery(String collectionName, ListForm form, Class<T> type){
		MongoCollection collection = MongoDBPlugin.getCollection(collectionName);
		MongoCursor<T> all = collection.find(form.reportingQuery).as(type);
		if (form.datatable) {
			// return ok(getUDTChunk(all)).as("application/json");
			return MongoStreamer.okStreamUDT(all);
		} else if(form.list) {
			// return ok(getChunk(all)).as("application/json");
			return MongoStreamer.okStream(all);
		} else if(form.count) {
			int count = all.count();
			Map<String, Integer> m = new HashMap<String, Integer>(1);
			m.put("result", count);
			return ok(Json.toJson(m));
		} else {
			return badRequest();
		}
	}

	/**
	 * Javascript routes.
	 * @param routes routes to provide as javascript
	 * @return       routes javascript
	 */
	public Result jsRoutes(play.api.routing.JavaScriptReverseRoute... routes) {
		return ok(JavaScriptReverseRouter.create("jsRoutes",routes)).as("text/javascript");
	}
	
	/*
	public Result index() {
	    java.io.File file = new java.io.File("/tmp/fileToServe.pdf");
	    java.nio.file.Path path = file.toPath();
	    Source<ByteString, ?> source = FileIO.fromPath(path);

	    Optional<Long> contentLength = Optional.of(file.length());

	    return new Result(
	        new ResponseHeader(200, Collections.emptyMap()),
	        new HttpEntity.Streamed(source, contentLength, Optional.of("text/plain"))
	    );
	}
	*/
	/*
	private static <T extends DBObject> Source<ByteString, ?> getChunk(MongoCursor<T> all) {
		return MongoStreamer.stream(all);
	}
	
	private static <T extends DBObject> Source<ByteString, ?> getUDTChunk(MongoCursor<T> all) {
		return MongoStreamer.streamUDT(all);
	}
	*/
/*	
	private static <T extends DBObject> StringChunks getChunk(MongoCursor<T> all) {
		return new StringChunks() {
			@Override
			public void onReady(Out<String> out) {
				Iterator<T> iter = all.iterator();
		    	out.write("[");
			    while (iter.hasNext()) {
			    	out.write(Json.toJson(iter.next()).toString());
		            if (iter.hasNext()) out.write(",");
		        }					
		        out.write("]");
			    out.close();					
			}
		};
	}
	
	private static <T extends DBObject> StringChunks getUDTChunk(MongoCursor<T> all) {
		return new StringChunks() {
			@Override
			public void onReady(Out<String> out) {
				out.write("{\"recordsNumber\":"+all.count()+",");
			    out.write("\"data\":[");
			    Iterator<T> iter = all.iterator();
			    while(iter.hasNext()){
			    	out.write(Json.toJson(iter.next()).toString());
		            if (iter.hasNext()) out.write(",");    	
			    }
			    out.write("]}");
			    out.close();				
			}
		};
	}
	*/
	
}
