package models.utils;

import models.utils.Model.Finder;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Object used to retrieve an object define in the NGL SQL DB.
 * In SQL, code is unique, it's the data processing label
 * 
 * @author galbini
 *
 */
public class ObjectSGBDReference<T extends Model<T>> implements IFetch<T>{
	
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
		Finder<T> find = new Finder<T>(className.getName()+"DAO");
		// TODO replace by generic SpringDAO find 
		//return Ebean.find(className).where()  
		//	      .eq("code",code).findUnique();
		return find.findByCode(code);
	}

}
