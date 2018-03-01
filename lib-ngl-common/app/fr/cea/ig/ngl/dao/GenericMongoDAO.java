package fr.cea.ig.ngl.dao;

import java.util.List;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate.Builder;

import com.mongodb.BasicDBObject;

import controllers.ListForm;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.MongoDBResult.Sort;
import models.utils.dao.DAOException;

public class GenericMongoDAO<T extends DBObject> {
	
	private final String   collectionName;
	private final Class<T> elementClass;
	
	public GenericMongoDAO(final String collectionName, final Class<T> elementClass) {
		this.collectionName = collectionName;
		this.elementClass   = elementClass;
	}
	
	// Fails hard if no instance is found
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
	 * A finder for mongoDB
	 * @param query
	 * @param orderBy
	 * @param orderSense
	 * @param limit
	 * @return a MongoDBResult
	 * @throws DAOException
	 */
	public MongoDBResult<T> mongoDBFinder(Query query, String orderBy, Sort orderSense, Integer limit) throws DAOException {
		MongoDBResult<T> results = MongoDBDAO.find(collectionName, elementClass, query).sort(orderBy, orderSense);
		if(limit != -1){
			results.limit(limit);
		}
		return results;
	}
	
	/**
	 * A finder for mongoDB
	 * @param query
	 * @param orderBy
	 * @param orderSense
	 * @param limit
	 * @param keys
	 * @return a MongoDBResult
	 * @throws DAOException
	 */
	public MongoDBResult<T> mongoDBFinder(Query query, String orderBy, Sort orderSense, Integer limit, BasicDBObject keys) throws DAOException {
		MongoDBResult<T> results = MongoDBDAO.find(collectionName, elementClass, query, keys).sort(orderBy, orderSense);
		if(limit != -1){
			results.limit(limit);
		}
		return results;
	}

	/**
	 * A finder for mongoDB without size limit of elements
	 * @param query
	 * @param orderBy
	 * @param orderSense
	 * @return a MongoDBResult
	 * @throws DAOException
	 */
	public MongoDBResult<T> mongoDBFinder(Query query, String orderBy, Sort orderSense) throws DAOException {
		return mongoDBFinder(query, orderBy, orderSense, -1);
	}
	
	/**
	 * A finder for mongoDB without size limit of elements
	 * @param query
	 * @param orderBy
	 * @param orderSense
	 * @param keys
	 * @return a MongoDBResult
	 * @throws DAOException
	 */
	public MongoDBResult<T> mongoDBFinder(Query query, String orderBy, Sort orderSense, BasicDBObject keys) throws DAOException {
		return mongoDBFinder(query, orderBy, orderSense, -1, keys);
	}
	
	/**
	 * A finder for mongoDB with pagination of results
	 * @param query
	 * @param orderBy
	 * @param orderSense
	 * @param pageNumber
	 * @param numberRecordsPerPage
	 * @return a MongoDBResult
	 * @throws DAOException
	 */
	public MongoDBResult<T> mongoDBFinderWithPagination(Query query, String orderBy, Sort orderSense, 
														Integer pageNumber, Integer numberRecordsPerPage) throws DAOException {
		return mongoDBFinder(query, orderBy, orderSense).page(pageNumber, numberRecordsPerPage);
	}
	
	/**
	 * A finder for mongoDB with pagination of results
	 * @param query
	 * @param orderBy
	 * @param orderSense
	 * @param pageNumber
	 * @param numberRecordsPerPage
	 * @param keys
	 * @return a MongoDBResult
	 * @throws DAOException
	 */
	public MongoDBResult<T> mongoDBFinderWithPagination(Query query, String orderBy, Sort orderSense, 
														Integer pageNumber, Integer numberRecordsPerPage, BasicDBObject keys) throws DAOException {
		return mongoDBFinder(query, orderBy, orderSense, keys).page(pageNumber, numberRecordsPerPage);
	}
}

