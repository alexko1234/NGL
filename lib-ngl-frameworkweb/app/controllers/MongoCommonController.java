package controllers;

import java.util.List;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate.Builder;

import org.bson.BSONObject;

import play.Logger;
import play.data.Form;
import play.libs.Json;
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
	
	protected MongoCommonController(String collectionName, Class<T> type) {
		super(type);
		this.collectionName = collectionName;
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
					.sort(form.orderBy, Sort.valueOf(form.orderSense))
					.limit(form.limit);
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
					.sort(form.orderBy, Sort.valueOf(form.orderSense))
					.limit(form.limit);
		}
		
		return results;
	}
	
	protected BasicDBObject getKeys(DatatableForm form) {
		BasicDBObject keys = new BasicDBObject();
		keys.putAll((BSONObject)getIncludeKeys(form.includes.toArray(new String[form.includes.size()])));
		keys.putAll((BSONObject)getExcludeKeys(form.excludes.toArray(new String[form.excludes.size()])));		
		return keys;
	}
	
	protected BasicDBObject getIncludeKeys(String[] keys) {
		BasicDBObject values = new BasicDBObject();
		for(String key : keys){
		    values.put(key, 1);
		}
		return values;
    }
	
	protected BasicDBObject getExcludeKeys(String[] keys) {
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
		T o =  getObject(code);		
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
		
	
}
