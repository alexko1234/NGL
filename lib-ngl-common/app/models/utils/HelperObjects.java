package models.utils;

import java.util.ArrayList;
import java.util.List;

import models.utils.dao.DAOException;
import play.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

// import play.Logger;

import fr.cea.ig.DBObject;

public class HelperObjects<T> {

	// private static final play.Logger.ALogger logger = Logger.of(HelperObjects.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@JsonIgnore
	public List<T> getObjects(Class<T> type, List<String> values) {
		List<T> objects = new ArrayList<T>();
		if (type.getSuperclass().getName().equals(DBObject.class.getName())) {
			for (int i = 0; i < values.size(); i++) {
				objects.add((T) new ObjectMongoDBReference(type,values.get(i)).getObject());				
			}
		} else {
			for (int i = 0; i < values.size(); i++) {
				try {
					objects.add( (T) new ObjectSGBDReference(type,values.get(i)).getObject());
				} catch (DAOException e) {
					Logger.error("getObject", e);
					throw new RuntimeException(e);
				}
			}
		}
		return objects;
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "hiding" })
	@JsonIgnore
	public <T> T getObject(Class<T> type, String value) {
		T object = null;
		if (type.getSuperclass().getName().equals(DBObject.class.getName())) {
			object = (T) new ObjectMongoDBReference(type, value).getObject();
		} else {
			try {
				object = (T) new ObjectSGBDReference(type, value).getObject();
			} catch (DAOException e) {
				Logger.error("getObject", e);
				throw new RuntimeException(e);
			}
		}
		return object;
	}

}
