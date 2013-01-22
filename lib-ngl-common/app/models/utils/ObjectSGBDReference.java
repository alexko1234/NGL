package models.utils;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.avaje.ebean.Ebean;

/**
 * Object used to retrieve an object define in the NGL SQL DB.
 * In SQL, code is unique, it's the data processing label
 * 
 * @author galbini
 *
 */
public class ObjectSGBDReference<T> implements IFetch<T>{
	
	@JsonIgnore
	private Class<T> className;
	
	public String code;
	
	public ObjectSGBDReference(Class<T> className, String code) {
		super();
		this.className = className;
		this.code = code;
	}
	
	public ObjectSGBDReference(){
		
	}
	
	public ObjectSGBDReference(Class<T> className){
		this.className = className;
	}

	@Override
	public T getObject() throws Exception {
		
		// code are in commonInfoType 
		return Ebean.find(className).where()  
			      .eq("code",code).findUnique();
	}

}
