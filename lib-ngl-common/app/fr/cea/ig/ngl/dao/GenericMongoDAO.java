package fr.cea.ig.ngl.dao;

import java.util.List;

import org.mongojack.DBQuery;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import models.utils.dao.DAOException;

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
	
}

