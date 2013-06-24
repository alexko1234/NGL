package models.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;
import validation.utils.ConstraintsHelper;
import fr.cea.ig.DBObject;

public class HelperObjects<T>{



	@SuppressWarnings({ "unchecked", "rawtypes" })
	@JsonIgnore
	public List<T> getObjects(Class<T> type, List<String> values, Map<String,List<ValidationError>> errors){
		List<T> objects =new ArrayList<T>();

		if(values!=null){
			if (type.getSuperclass().getName().equals(DBObject.class.getName())){

				for (int i = 0; i < values.size(); i++) {
					try {
						objects.add((T) new ObjectMongoDBReference(type,values.get(i)).getObject());
					} catch (Exception e) {
						// TODO
					}	
				}
			}	else {

				for (int i = 0; i < values.size(); i++) {
					try {
						objects.add( (T) new ObjectSGBDReference(type,values.get(i)).getObject());
					} catch (Exception e) {
						// TODO
					}	
				}
			}
		}
			return objects;
		}

		public List<T> getObjects(Class<T> type, List<String> values){
			return  getObjects( type,  values, null);
		}


		@SuppressWarnings({ "unchecked", "rawtypes" })
		@JsonIgnore
		public <T> T getObject(Class<T> type, String value,Map<String, List<ValidationError>> errors){
			T object = null ;
						
			if (type.getSuperclass().getName().equals(DBObject.class.getName())){

				try {
					object=(T) new ObjectMongoDBReference(type,value).getObject();
				} catch (Exception e) {
					if(errors !=null){
						ConstraintsHelper.addErrors(errors,"code", "error.codeNotFound",type, value);
					}
					//TODO log error !
				}	

			}	else {

				try {
					object=(T) new ObjectSGBDReference(type,value).getObject();
				} catch (Exception e) {
					if(errors !=null){
						ConstraintsHelper.addErrors(errors,"code", "error.codeNotFound",type,value);
					}
					//TODO log error !
				}	

			}

			if(object==null && errors!=null){
				ConstraintsHelper.addErrors(errors,"code", "error.codeNotFound",type,value);
			}
			return object;
		}

	}
