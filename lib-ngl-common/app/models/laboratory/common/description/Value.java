package models.laboratory.common.description;

import java.util.List;

import models.laboratory.common.description.dao.ValueDAO;
import models.utils.Model;
import models.utils.dao.AbstractDAO;
import models.utils.dao.DAOException;

/**
 * Possible value of property definition
 * @author ejacoby
 *
 */
public class Value extends Model<Value> {

	public static final ValueFinder find = new ValueFinder(); 

	public String value;  //used as code but not rename because strong impact will be remove after

	public String name;

	public Boolean defaultValue = Boolean.FALSE;

	public Value() {
		super(ValueDAO.class.getName());
	}

	@Override
	protected Class<? extends AbstractDAO<Value>> daoClass() {
		return ValueDAO.class;
	}
	
	public static class ValueFinder extends Finder<Value,ValueDAO> {

//			public ValueFinder() {
//			    super(ValueDAO.class.getName());
//			}
		public ValueFinder() { super(ValueDAO.class);	}

		public List<Value> findUnique(String propertyDefinitionCode) throws DAOException {
//			return ((ValueDAO)getInstance()).findUnique(propertyDefinitionCode);
			return getInstance().findUnique(propertyDefinitionCode);
		}
		
	}

}
