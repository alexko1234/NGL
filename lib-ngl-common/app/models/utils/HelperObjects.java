package models.utils;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.db.ebean.Model;
import fr.cea.ig.DBObject;

public class HelperObjects<T>{

	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@JsonIgnore
	public List<T> getObjects(Class<T> type, List<String> values){
		List<T> objects =new ArrayList<T>();

		if (type.getSuperclass().equals(DBObject.class)){

			for (int i = 0; i < values.size(); i++) {
				try {
					objects.add((T) new ObjectMongoDBReference(type.getClass(),values.get(i)).getObject());
				} catch (Exception e) {
					// TODO
				}	
			}
		}	else if (type.getSuperclass().equals(Model.class)) {

			for (int i = 0; i < values.size(); i++) {
				try {
					objects.add( (T) new ObjectSGBDReference(type.getClass(),values.get(i)).getObject());
				} catch (Exception e) {
					// TODO
				}	
			}
		}
		
		return objects;
	}

}
