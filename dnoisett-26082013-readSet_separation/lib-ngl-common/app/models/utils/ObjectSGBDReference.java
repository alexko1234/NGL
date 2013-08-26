package models.utils;

import models.utils.Model.Finder;
import models.utils.dao.DAOException;

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
	public T getObject() throws DAOException {
		// init Finder from class DAO associated to this class in package ./dao
		Finder<T> find = new Finder<T>(className.getName().replaceAll("description", "description.dao")+"DAO");
		return find.findByCode(code);
	}

}
