package models.laboratory.common.description;

import java.util.LinkedHashMap;
import java.util.Map;

import models.laboratory.common.description.dao.ObjectTypeDAO;
import play.modules.spring.Spring;

public class ObjectType{
	
	public Long id;
	
	public String type;
	
	public Boolean generic;
	
	public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
        for(ObjectType c: objectTypeDAO.findAll()) {
            options.put(c.id.toString(), c.type);
        }
        return options;
    }

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
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getGeneric() {
		return generic;
	}

	public void setGeneric(Boolean generic) {
		this.generic = generic;
	}
	
	
}
