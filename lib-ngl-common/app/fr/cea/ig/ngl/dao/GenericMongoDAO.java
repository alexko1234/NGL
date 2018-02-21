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
	 * @param form
	 * @param query
	 * @return a MongoDBResult
	 */
	public MongoDBResult<T> mongoDBFinder(ListForm form,  Query query) throws DAOException {
		MongoDBResult<T> results = null;
		if (form.datatable) {
			results = MongoDBDAO.find(collectionName, elementClass, query) 
								.sort(form.orderBy, Sort.valueOf(form.orderSense));
			if(form.isServerPagination()){
				results.page(form.pageNumber,form.numberRecordsPerPage); 
			}
		} else {
			results = MongoDBDAO.find(collectionName, elementClass, query) 
								.sort(form.orderBy, Sort.valueOf(form.orderSense));
			if(form.limit != -1){
				results.limit(form.limit);
			}
		}
		return results;
	}
	
}

