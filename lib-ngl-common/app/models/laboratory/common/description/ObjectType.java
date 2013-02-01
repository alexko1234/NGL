package models.laboratory.common.description;

import models.laboratory.common.description.dao.ObjectTypeDAO;
import play.modules.spring.Spring;

/**
 * Type definition
 * @author ejacoby
 *
 */
public class ObjectType{
	
	public Long id;
	
	public String type;
	
	//Set true if type has additional attributes compared to commonInfoType
	public Boolean generic;
	
	
	public static ObjectType findByType(String type)
	{
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		return objectTypeDAO.find(type);
	}
	
	public static ObjectType findById(long id)
	{
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		return objectTypeDAO.findById(id);
	}
	
	
	
}
