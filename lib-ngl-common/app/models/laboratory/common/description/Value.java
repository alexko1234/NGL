package models.laboratory.common.description;

import models.laboratory.common.description.dao.ValueDAO;
import models.utils.Model;

/**
 * Possible value of property definition
 * @author ejacoby
 *
 */
public class Value extends Model<Value>{

	public String value;   
	
	public Boolean defaultValue = Boolean.FALSE;

	public static Finder<Value> find = new Finder<Value>(ValueDAO.class.getName()); 
	
	public Value() {
		super(ValueDAO.class.getName());
	}
    
}
