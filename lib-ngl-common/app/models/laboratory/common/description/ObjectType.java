package models.laboratory.common.description;

import java.io.Serializable;

import models.laboratory.common.description.dao.ObjectTypeDAO;
import models.utils.dao.DAOException;
import play.modules.spring.Spring;

/**
 * Type definition
 * @author ejacoby
 *
 */
public class ObjectType implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Long id;
	
	public String code;
	
	//Set true if type has additional attributes compared to commonInfoType
	public Boolean generic;
	
	
	public static ObjectType findByCode(String code) throws DAOException
	{
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		return objectTypeDAO.findByCode(code);
	}
	
	public static ObjectType findById(long id) throws DAOException
	{
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		return objectTypeDAO.findById(id);
	}
	
	
	
}
