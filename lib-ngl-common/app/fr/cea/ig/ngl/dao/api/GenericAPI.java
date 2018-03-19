package fr.cea.ig.ngl.dao.api;

import java.util.List;
import java.util.stream.Collectors;

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
import play.Logger;
import validation.ContextValidation;

public abstract class GenericAPI<O extends GenericMongoDAO<T>, T extends DBObject> {

	protected final O dao; 

	@Inject
	public GenericAPI(O dao) {
		this.dao = dao;
	}


	/**
	 * @return the list of field of DBObject which could be updated
	 */
	protected abstract List<String> authorizedUpdateFields();

	/**
	 * @return Default keys of DBObject
	 */
	protected abstract List<String> defaultKeys();

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
		return dao.mongoDBFinderWithPagination(query, orderBy, orderSense, pageNumber, numberRecordsPerPage, keys).toList();
	}

	public Source<ByteString, ?> streamUDT(Query query, String orderBy, Sort orderSense, BasicDBObject keys, 
			Integer pageNumber, Integer numberRecordsPerPage) {
		return MongoStreamer.streamUDT(dao.mongoDBFinderWithPagination(query, orderBy, orderSense, pageNumber, numberRecordsPerPage, keys));
	}
	
	public Source<ByteString, ?> streamUDTWithDefaultKeys(Query query, String orderBy, Sort orderSense,
			Integer pageNumber, Integer numberRecordsPerPage) {
		return MongoStreamer.streamUDT(dao.mongoDBFinderWithPagination(query, orderBy, orderSense, pageNumber, numberRecordsPerPage, defaultDBKeys()));
	}
	
	public Source<ByteString, ?> streamUDTWithDefaultKeys(Query query, String orderBy, Sort orderSense, Integer limit) {
		return MongoStreamer.streamUDT(dao.mongoDBFinder(query, orderBy, orderSense, limit, defaultDBKeys()));
	}

	public Source<ByteString, ?> streamUDT(Query query, String orderBy, Sort orderSense, BasicDBObject keys, Integer limit) {
		return MongoStreamer.streamUDT(dao.mongoDBFinder(query, orderBy, orderSense, limit, keys));
	}
	
	public Source<ByteString, ?> stream(Query query, String orderBy, Sort orderSense, BasicDBObject keys, Integer limit) {
		return MongoStreamer.stream(dao.mongoDBFinder(query, orderBy, orderSense, limit, keys));
	}

	public Source<ByteString, ?> streamUDT(Query query, String orderBy, Sort orderSense, BasicDBObject keys) {
		return MongoStreamer.streamUDT(dao.mongoDBFinder(query, orderBy, orderSense, keys));
	}

	/* ---- method often used in reporting context ---- */
	public MongoCursor<T> findByQuery(String reportingQuery) {
		return dao.findByQuery(reportingQuery);
	}

	public Integer count(String reportingQuery){
		return findByQuery(reportingQuery).count();
	}

	public Source<ByteString, ?> stream(String reportingQuery){
		return MongoStreamer.streamUDT(findByQuery(reportingQuery));
	}
	/* ------------------------------------------------ */

	
	/**
	 * @return BasicDBObject which corresponds to the list of default keys from {@link GenericAPI#defaultKeys()}
	 */
	protected BasicDBObject defaultDBKeys() {
		BasicDBObject keys = new BasicDBObject();
		keys.putAll(defaultKeys().stream().collect(Collectors.toMap(k -> k, k -> 1)));
		// same as
		// for(String k : defaultKeys()) {
		//	keys.put(k, 1);
		// }
		return keys;
		
		
	}


	public void checkAuthorizedUpdateFields(ContextValidation ctxVal, List<String> authorizedFields, List<String> fields) {
		for (String field: fields) {
			if (!authorizedFields.contains(field)) {
				ctxVal.addErrors("fields", "error.valuenotauthorized", field);
			}
		}
	}


	protected void checkIfFieldsAreDefined(ContextValidation ctxVal, List<String> fields, T s) {
		for(String field: fields){
			try {
				if(s.getClass().getField(field).get(s) == null){
					ctxVal.addErrors(field, "error.notdefined");
				}
			} catch(Exception e){
				Logger.error(e.getMessage());
			}
		}
	}	
}
