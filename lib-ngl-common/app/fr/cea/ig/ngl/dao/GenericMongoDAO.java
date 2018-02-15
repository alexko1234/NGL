package fr.cea.ig.ngl.dao;

import java.util.List;

import org.mongojack.DBQuery;

import com.mongodb.MongoException;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import models.utils.dao.DAOException;

// Need probably a proper mongoexception wrapping all around.
public class GenericMongoDAO<T extends DBObject> {
	
	private final String   collectionName;
	private final Class<T> elementClass;
	
	public GenericMongoDAO(final String collectionName, final Class<T> elementClass) {
		this.collectionName = collectionName;
		this.elementClass   = elementClass;
	}
	
	// Fails hard if no instance is found
	public T findOne(DBQuery.Query q) throws DAOException {
		T t = MongoDBDAO.findOne(collectionName, elementClass, q);
		if (t == null)
			throw new DAOException("no instance found");
		return t;
	}
	
	public Iterable<T> find(DBQuery.Query q) throws DAOException {
		return MongoDBDAO.find(collectionName, elementClass, q).cursor;
	}
	
	public List<T> findAsList(DBQuery.Query q) throws DAOException {
		return MongoDBDAO.find(collectionName, elementClass, q).toList();
	}
	
	public Iterable<T> all() throws DAOException {
		return MongoDBDAO.find(collectionName, elementClass).cursor;
	}
	
	public T getByCode(String code) throws DAOException {
		T t = MongoDBDAO.findById(collectionName, elementClass, code);
		if (t == null)
			throw notFound(code);
		return t;
	}

	private DAOException notFound(String code) {
		return new DAOEntityNotFoundException("could not find '" + code + "' in collection '" + collectionName + "'");
	}
	
	public T save(T t) throws DAOException {
		try {
			return MongoDBDAO.save(collectionName, t);
		} catch (MongoException e) {
			throw new DAOException(e);
		}
	}
	
	public T update(T t) throws DAOException {
		try {
			return MongoDBDAO.update(collectionName,t).getSavedObject();
		} catch (MongoException e) {
			throw new DAOException(e);
		}
	}
	
}

