package fr.cea.ig.ngl.dao;

import java.util.List;

import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate.Builder;

import com.mongodb.BasicDBObject;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.MongoDBResult.Sort;
import models.utils.dao.DAOException;
import play.modules.jongo.MongoDBPlugin;

// TODO transformer cette classe en classe abstraite si on décide d'utiliser l'héritage au lieu d'une association d'objet pour les DAO "concrêtes"
public /*abstract*/ class GenericMongoDAO<T extends DBObject> {
	
	private final String   collectionName;
	private final Class<T> elementClass;
	
	public GenericMongoDAO(final String collectionName, final Class<T> elementClass) {
		this.collectionName = collectionName;
		this.elementClass   = elementClass;
	}
	
	/**
	 * Throw exception if no instance is found.
	 * @param  q            Query
	 * @return              T the DBObject
	 * @throws DAOException if no instance is found
	 */
	public T findOne(Query q) throws DAOException {
		T t = MongoDBDAO.findOne(collectionName, elementClass, q);
		if (t == null)
			throw new DAOException("no instance found");
		return t;
	}
	
	public Iterable<T> find(Query q) throws DAOException {
		return MongoDBDAO.find(collectionName, elementClass, q).cursor;
	}
	
	public List<T> findAsList(Query q) throws DAOException {
		return MongoDBDAO.find(collectionName, elementClass, q).toList();
	}
	
	public Iterable<T> all() throws DAOException {
		return MongoDBDAO.find(collectionName, elementClass).cursor;
	}
	
	public T findByCode(String code) throws DAOException {
		return MongoDBDAO.findByCode(collectionName, elementClass, code);
	}
	
	public T getObject(String code, BasicDBObject keys) throws DAOException {
    	return MongoDBDAO.findByCode(collectionName, elementClass, code, keys);
    }
	
	public T getObject(String code) throws DAOException {
		return this.findByCode(code);
	}
	
	public T getObject(Query query) throws DAOException {
    	return this.findOne(query);
    }
	
	public boolean isObjectExist(String code) throws DAOException {
		return MongoDBDAO.checkObjectExistByCode(collectionName, elementClass, code);
	}
	
	public boolean isObjectExist(Query query) throws DAOException {
		return MongoDBDAO.checkObjectExist(collectionName, elementClass, query);
	}
	
	public T saveObject(T o) throws DAOException {
		return MongoDBDAO.save(collectionName, o);
	}
	
	public void updateObject(T o) throws DAOException {
		MongoDBDAO.update(collectionName, o);
	}
	
	public void updateObject(Query query, Builder builder) throws DAOException {
		MongoDBDAO.update(collectionName, elementClass, query, builder);
	}
	
	public void deleteObject(String code) throws DAOException {
		MongoDBDAO.deleteByCode(collectionName,  elementClass, code);
	}
	
	/**
	 * A finder for mongoDB.
	 * @param query      Query
	 * @param orderBy    String
	 * @param orderSense how to sort the results
	 * @param limit      if it is set to -1 then results are unlimited  
	 * @return           a MongoDBResult
	 * @throws DAOException exception
	 */
	public MongoDBResult<T> mongoDBFinder(Query query, String orderBy, Sort orderSense, Integer limit) throws DAOException {
		MongoDBResult<T> results = MongoDBDAO.find(collectionName, elementClass, query).sort(orderBy, orderSense);
		if(limit != -1){
			results.limit(limit);
		}
		return results;
	}
	
	/**
	 * A finder for mongoDB.
	 * @param query      Query
	 * @param orderBy    String
	 * @param orderSense how to sort the results
	 * @param limit      if it is set to -1 then results are unlimited  
	 * @param keys       map to restrict keys of object
	 * @return           a MongoDBResult
	 * @throws DAOException exception
	 */
	public MongoDBResult<T> mongoDBFinder(Query query, String orderBy, Sort orderSense, Integer limit, BasicDBObject keys) throws DAOException {
		MongoDBResult<T> results = MongoDBDAO.find(collectionName, elementClass, query, keys).sort(orderBy, orderSense);
		if (limit != -1) {
			results.limit(limit);
		}
		return results;
	}

	/**
	 * A finder for mongoDB without size limit of elements.
	 * @param query      Query
	 * @param orderBy    String
	 * @param orderSense how to sort the results
	 * @return           a MongoDBResult
	 * @throws DAOException exception
	 */
	public MongoDBResult<T> mongoDBFinder(Query query, String orderBy, Sort orderSense) throws DAOException {
		return mongoDBFinder(query, orderBy, orderSense, -1);
	}
	
	/**
	 * A finder for mongoDB without size limit of elements.
	 * @param query      Query
	 * @param orderBy    String
	 * @param orderSense how to sort the results
	 * @param keys       map to restrict keys of object
	 * @return           a MongoDBResult
	 * @throws DAOException exception
	 */
	public MongoDBResult<T> mongoDBFinder(Query query, String orderBy, Sort orderSense, BasicDBObject keys) throws DAOException {
		return mongoDBFinder(query, orderBy, orderSense, -1, keys);
	}
	
	/**
	 * A finder for mongoDB with pagination of results
	 * @param query Query
	 * @param orderBy String
	 * @param orderSense how to sort the results
	 * @param pageNumber Integer
	 * @param numberRecordsPerPage Integer
	 * @return a MongoDBResult
	 * @throws DAOException exception
	 */
	public MongoDBResult<T> mongoDBFinderWithPagination(Query query, String orderBy, Sort orderSense, 
														Integer pageNumber, Integer numberRecordsPerPage) throws DAOException {
		return mongoDBFinder(query, orderBy, orderSense).page(pageNumber, numberRecordsPerPage);
	}
	
	/**
	 * A finder for mongoDB with pagination of results.
	 * @param query                Query
	 * @param orderBy              String
	 * @param orderSense           how to sort the results
	 * @param pageNumber           Integer
	 * @param numberRecordsPerPage Integer
	 * @param keys                 map to restrict keys of object
	 * @return                     a MongoDBResult
	 * @throws DAOException exception
	 */
	public MongoDBResult<T> mongoDBFinderWithPagination(Query query, String orderBy, Sort orderSense, 
														Integer pageNumber, Integer numberRecordsPerPage, BasicDBObject keys) throws DAOException {
		return mongoDBFinder(query, orderBy, orderSense, keys).page(pageNumber, numberRecordsPerPage);
	}

	/**
	 * use to replace controllers.MongoCommonController.nativeMongoDBQuery(ListForm form).
	 * @param query String
	 * @return      mongo cursor
	 */
	public MongoCursor<T> findByQuery(String query) {
//		MongoCollection collection = MongoDBPlugin.getCollection(this.collectionName);
//		return (MongoCursor<T>) collection.find(query).as(elementClass);
		MongoCollection collection = MongoDBPlugin.getCollection(collectionName);
		return collection.find(query).as(elementClass);
	}
	
	/**
	 * Construct a builder from some fields.
	 * Use to update a mongodb document.
	 * @param value  DBObject
	 * @param fields {@literal List<String>}
	 * @return       Builder
	 */
	public Builder getBuilder(T value, List<String> fields) {
		return getBuilder(value, fields, null);
	}
	
	/**
	 * Construct a builder from some fields.
	 * Use to update a mongodb document.
	 * @param value  DBObject
	 * @param fields {@literal List<String>}
	 * @param prefix String
	 * @return       Builder
	 */
	public Builder getBuilder(T value, List<String> fields, String prefix) {
		Builder builder = new Builder();
		try {
			for (String field: fields) {
				String fieldName = (null != prefix) ? prefix + "." + field : field;
				builder.set(fieldName, elementClass.getField(field).get(value));
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return builder;
	}
	
}

