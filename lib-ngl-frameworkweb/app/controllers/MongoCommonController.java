package controllers;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.BSONObject;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate.Builder;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.modules.jongo.MongoDBPlugin;
import play.mvc.Result;
import validation.ContextValidation;
import views.components.datatable.DatatableForm;

import com.mongodb.BasicDBObject;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.MongoDBResult.Sort;


public abstract class MongoCommonController<T extends DBObject> extends APICommonController<T>{

	protected String collectionName;
	protected List<String> defaultKeys;
	
	
	protected MongoCommonController(String collectionName, Class<T> type) {
		super(type);
		this.collectionName = collectionName;
	}
	
	protected MongoCommonController(String collectionName, Class<T> type, List<String> defaultKeys) {
		super(type);
		this.collectionName = collectionName;
		this.defaultKeys = defaultKeys;
	}

	protected T getObject(String code, BasicDBObject keys) {
    	return MongoDBDAO.findByCode(collectionName, type, code, keys);
    }
	
	protected T getObject(String code) {
    	return MongoDBDAO.findByCode(collectionName, type, code);
    }
	
	protected T getObject(Query query) {
    	return MongoDBDAO.findOne(collectionName, type, query);
    }
	
	protected boolean isObjectExist(String code){
		return MongoDBDAO.checkObjectExistByCode(collectionName, type, code);
	}
	
	protected boolean isObjectExist(Query query){
		return MongoDBDAO.checkObjectExist(collectionName, type, query);
	}
	
	protected T saveObject(T o){
		return MongoDBDAO.save(collectionName, o);
	}
	
	protected void updateObject(T o){
		MongoDBDAO.update(collectionName, o);
	}
	
	protected void updateObject(Query query, Builder builder){
		MongoDBDAO.update(collectionName, type, query, builder);
	}
	
	protected void deleteObject(String code){
		MongoDBDAO.deleteByCode(collectionName,  type, code);
	}
	
	/**
	 * A finder for mongoDB
	 * @param collection
	 * @param form
	 * @param type (DBObject)
	 * @param query
	 * @return a MongoDBResult
	 */
	protected MongoDBResult<T> mongoDBFinder(ListForm form,  DBQuery.Query query){
		MongoDBResult<T> results = null;
		if(form.datatable){
			results = MongoDBDAO.find(collectionName, type, query) 
					.sort(form.orderBy, Sort.valueOf(form.orderSense));
			if(form.isServerPagination()){
				results.page(form.pageNumber,form.numberRecordsPerPage); 
			}
		}else{
			results = MongoDBDAO.find(collectionName, type, query) 
					.sort(form.orderBy, Sort.valueOf(form.orderSense));
			if(form.limit != -1){
				results.limit(form.limit);
			}
		}
		
		return results;
	}

	protected MongoDBResult<T> mongoDBFinder(ListForm form, DBQuery.Query query, BasicDBObject keys){
		MongoDBResult<T> results = null;
		
		if(form.datatable){
			results = MongoDBDAO.find(collectionName, type, query, keys) 
					.sort(form.orderBy, Sort.valueOf(form.orderSense));
			if(form.isServerPagination()){
				results.page(form.pageNumber,form.numberRecordsPerPage); 
			}
		}else{
			results = MongoDBDAO.find(collectionName, type, query, keys) 
					.sort(form.orderBy, Sort.valueOf(form.orderSense));
			if(form.limit != -1){
				results.limit(form.limit);
			}
		}
		
		return results;
	}
	
	protected BasicDBObject getKeys(DatatableForm form) {
		BasicDBObject keys = new BasicDBObject();
		if(!form.includes.contains("*")){
			keys.putAll((BSONObject)getIncludeKeys(form.includes.toArray(new String[form.includes.size()])));
		}
		keys.putAll((BSONObject)getExcludeKeys(form.excludes.toArray(new String[form.excludes.size()])));		
		return keys;
	}
	
	protected BasicDBObject getIncludeKeys(String[] keys) {
		Arrays.sort(keys, Collections.reverseOrder());
		BasicDBObject values = new BasicDBObject();
		for(String key : keys){
		    values.put(key, 1);
		}
		return values;
    }
	
	protected BasicDBObject getExcludeKeys(String[] keys) {
		Arrays.sort(keys, Collections.reverseOrder());
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
	protected Builder getBuilder(Object value, List<String> fields) {
		return getBuilder(value, fields, type, null);
	}
	
	
	/**
	 * Construct a builder from some fields
	 * Use to update a mongodb document
	 * @param value
	 * @param fields
	 * @param clazz
	 * @return
	 */
	protected <P> Builder getBuilder(Object value, List<String> fields, Class<P> clazz, String prefix) {
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
	/**
	 * Validate authorized field for specific update field
	 * @param ctxVal
	 * @param fields
	 * @param authorizedUpdateFields
	 */
	protected void validateAuthorizedUpdateFields(ContextValidation ctxVal, List<String> fields,
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
	protected void validateIfFieldsArePresentInForm(
			ContextValidation ctxVal, List<String> fields, Form<?> filledForm) {
		Object o = filledForm.get();
		for(String field: fields){
			try {
				if(o.getClass().getField(field).get(o) == null){
					ctxVal.addErrors(field, "error.notdefined");
				}
			}catch(Exception e){
				Logger.error(e.getMessage());
			}
		}	
		
	}
	
	//@Permission(value={"delete_readset"}) 
	public Result delete(String code) { 
		T objectInDB =  getObject(code);
		if(objectInDB == null) {
			return notFound();
		}
		deleteObject(code);
		return ok();
	}
	
	
	public Result get(String code) {
		DatatableForm form = filledFormQueryString(DatatableForm.class);
		T o =  getObject(code, getKeys(updateForm(form)));		
		if(o == null) {
			return notFound();
		} 
		return ok(Json.toJson(o));		
	}
		
	//@Permission(value={"reading"})
	public Result head(String code) {
		if(!isObjectExist(code)){			
			return notFound();					
		}
		return ok();	
	}
		
	protected Result nativeMongoDBQuery(ListForm form){
		MongoCollection collection = MongoDBPlugin.getCollection(collectionName);
		MongoCursor<T> all = (MongoCursor<T>) collection.find(form.reportingQuery).as(type);
		if(form.datatable){
			return ok(getUDTChunk(all)).as("application/json");
		}else if(form.list){
			return ok(getChunk(all)).as("application/json");									
		}else if(form.count){
			int count = all.count();
			Map<String, Integer> m = new HashMap<String, Integer>(1);
			m.put("result", count);
			return ok(Json.toJson(m));
		}else{
			return badRequest();
		}
	}
	
	private StringChunks getChunk(MongoCursor<T> all) {
		return new StringChunks() {
			@Override
			public void onReady(Out<String> out) {
				Iterator<T> iter = all.iterator();
		    	out.write("[");
			    while (iter.hasNext()) {
			    	out.write(Json.toJson(iter.next()).toString());
		            if(iter.hasNext())out.write(",");
		        }					
		        out.write("]");
			    out.close();					
			}
		};
	}
	
	private StringChunks getUDTChunk(MongoCursor<T> all) {
		return new StringChunks() {
			@Override
			public void onReady(Out<String> out) {
				out.write("{\"recordsNumber\":"+all.count()+",");
			    out.write("\"data\":[");
			    Iterator<T> iter = all.iterator();
			    while(iter.hasNext()){
			    	out.write(Json.toJson(iter.next()).toString());
		            if(iter.hasNext())out.write(",");    	
			    }
			    out.write("]}");
			    out.close();				
			}
		};
	}
	
	protected Result mongoJackQuery(ListForm searchForm, Query query) {
		BasicDBObject keys = getKeys(updateForm(searchForm));
		
		if(searchForm.datatable){
			MongoDBResult<T> results =  mongoDBFinder(searchForm, query,keys);
			return ok(getUDTChunk(results)).as("application/json");
		}else if (searchForm.list){
			keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);
			if(null == searchForm.orderBy)searchForm.orderBy = "code";
			if(null == searchForm.orderSense)searchForm.orderSense = 0;				

			MongoDBResult<T> results = mongoDBFinder(searchForm, query, keys);
			return ok(getLOChunk(results)).as("application/json");
		}else if(searchForm.count){
			keys = new BasicDBObject();
			keys.put("_id", 1);//Don't need the _id field
			MongoDBResult<T> results =  mongoDBFinder(searchForm, query);
			Map<String, Integer> m = new HashMap<String, Integer>(1);
			m.put("result", results.count());
			return ok(Json.toJson(m));
		}else{
			if(null == searchForm.orderBy)searchForm.orderBy = "code";
			if(null == searchForm.orderSense)searchForm.orderSense = 0;
			MongoDBResult<T> results = mongoDBFinder(searchForm, query,keys);
			return ok(getChunk(results)).as("application/json");	
		}
	}
	
	protected DatatableForm updateForm(DatatableForm form) {
		if(form.includes.contains("default")){
			form.includes.remove("default");
			if(null != defaultKeys){
				form.includes.addAll(defaultKeys);
			}
			
		}
		return form;
	}
	
	private StringChunks getUDTChunk(MongoDBResult<T> all) {
		return new StringChunks() {
			@Override
			public void onReady(Out<String> out) {
				out.write("{\"recordsNumber\":"+all.count()+",");
			    out.write("\"data\":[");
			    Iterator<T> iter = all.getCursor();
			    while(iter.hasNext()){
			    	out.write(Json.toJson(iter.next()).toString());
		            if(iter.hasNext())out.write(",");    	
			    }
			    out.write("]}");
			    out.close();				
			}
		};
	}
	
	private StringChunks getChunk(MongoDBResult<T> all) {
		return new StringChunks() {
			@Override
			public void onReady(Out<String> out) {
				Iterator<T> iter = all.getCursor();
		    	out.write("[");
			    while (iter.hasNext()) {
			    	out.write(Json.toJson(iter.next()).toString());
		            if(iter.hasNext())out.write(",");
		        }					
		        out.write("]");
			    out.close();					
			}
		};
	}
	//TODO Beter implementation to choose which property must be used to populate list_object
	//the better way is to implement getListObject inside DBObject
	private StringChunks getLOChunk(MongoDBResult<T> all) {
		return new StringChunks() {
			@Override
			public void onReady(Out<String> out) {
				Iterator<T> iter = all.getCursor();
		    	out.write("[");
			    while (iter.hasNext()) {
			    	T o = iter.next();
			    	out.write(Json.toJson(new ListObject(o.code, o.code)).toString());
		            if(iter.hasNext())out.write(",");
		        }					
		        out.write("]");
			    out.close();					
			}
		};
	}
}
