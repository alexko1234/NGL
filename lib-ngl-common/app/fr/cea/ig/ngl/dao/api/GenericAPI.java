package fr.cea.ig.ngl.dao.api;

import java.util.List;

import javax.inject.Inject;

import org.jongo.MongoCursor;
import org.mongojack.DBQuery.Query;

import com.mongodb.BasicDBObject;

import akka.stream.javadsl.Source;
import akka.util.ByteString;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBResult.Sort;
import fr.cea.ig.mongo.MongoStreamer;
import fr.cea.ig.ngl.dao.GenericMongoDAO;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import play.Logger;
import validation.ContextValidation;

public abstract class GenericAPI<O extends GenericMongoDAO<T>, T extends DBObject> {

	private final O dao; 
	
	public O dao() {
		return dao;
	}

	@Inject
	public GenericAPI(O dao) {
		this.dao = dao;
	}

	public abstract T create(T input, String currentUser) throws APIValidationException, APIException;
	public abstract T update(T input, String currentUser) throws APIException, APIValidationException;
	public abstract T update(T input, String currentUser, List<String> fields) throws APIException, APIValidationException;

	public void delete(String code) {
		this.dao.deleteObject(code);
	}
	
	/**
	 * Find an object by code in db
	 * @param code
	 * @return
	 */
	public T get(String code) {
		return dao.findByCode(code);
	}
	
	/**
	 * get an object by specifying keys
	 * @param code
	 * @param keys
	 * @return
	 */
	public T getObject(String code, BasicDBObject keys) {
		return dao.getObject(code, keys);
	}
	
	/**
	 * check if an object exists in db
	 * @param code
	 * @return
	 */
	public boolean isObjectExist(String code) {
		return dao.isObjectExist(code);
	}
	
	public List<T> list(Query query, String orderBy, Sort orderSense) {
		return dao.mongoDBFinder(query, orderBy, orderSense).toList();
	}
	
	public List<T> list(Query query, String orderBy, Sort orderSense, BasicDBObject keys, Integer limit) {
		return dao.mongoDBFinder(query, orderBy, orderSense, limit, keys).toList();
	}
	
	public List<T> list(Query query, String orderBy, Sort orderSense, BasicDBObject keys) {
		return list(query, orderBy, orderSense, keys, -1);
	}
	
	public List<T> list(Query query, String orderBy, Sort orderSense, BasicDBObject keys, 
			Integer pageNumber, Integer numberRecordsPerPage) {
		return dao().mongoDBFinderWithPagination(query, orderBy, orderSense, pageNumber, numberRecordsPerPage, keys).toList();
	}
	
	public Source<ByteString, ?> stream(Query query, String orderBy, Sort orderSense, BasicDBObject keys, 
			Integer pageNumber, Integer numberRecordsPerPage) {
		return MongoStreamer.streamUDT(dao.mongoDBFinderWithPagination(query, orderBy, orderSense, pageNumber, numberRecordsPerPage, keys));
	}
	
	public Source<ByteString, ?> stream(Query query, String orderBy, Sort orderSense, BasicDBObject keys, Integer limit) {
		return MongoStreamer.streamUDT(dao.mongoDBFinder(query, orderBy, orderSense, limit, keys));
	}
	
	public Source<ByteString, ?> stream(Query query, String orderBy, Sort orderSense, BasicDBObject keys) {
		return MongoStreamer.streamUDT(dao().mongoDBFinder(query, orderBy, orderSense, keys));
	}
	
	/* ---- method often used in reporting context ---- */
	public MongoCursor<T> findByQuery(String reportingQuery) {
		return dao().findByQuery(reportingQuery);
	}
	
	public Integer count(String reportingQuery){
		return findByQuery(reportingQuery).count();
	}
	
	public Source<ByteString, ?> stream(String reportingQuery){
		return MongoStreamer.streamUDT(findByQuery(reportingQuery));
	}
	/* ------------------------------------------------ */
	
	
	/**
	 * Validate field list
	 * @param ctxVal
	 * @param authorizedFields
	 * @param fields
	 * @return
	 */
	public ContextValidation checkAuthorizedUpdateFields(ContextValidation ctxVal, List<String> authorizedFields, List<String> fields) {
		for (String field: fields) {
			if (!authorizedFields.contains(field)) {
				ctxVal.addErrors("fields", "error.valuenotauthorized", field);
			}
		}
		return ctxVal;
	}
	
	/**
	 * Validate if the fields exist in Object 
	 * @param ctxVal
	 * @param fields
	 * @param filledForm
	 */
	protected ContextValidation checkIfFieldsAreDefined(ContextValidation ctxVal, List<String> fields, T s) {
		for(String field: fields){
			try {
				if(s.getClass().getField(field).get(s) == null){
					ctxVal.addErrors(field, "error.notdefined");
				}
			} catch(Exception e){
				Logger.error(e.getMessage());
			}
		}	
		return ctxVal;
	}	
}
