package models.laboratory.common.description.dao;

import models.laboratory.common.description.ObjectType;
import models.utils.dao.AbstractDAO;

import org.springframework.stereotype.Repository;

@Repository
public class ObjectTypeDAO extends AbstractDAO<ObjectType>{

	protected ObjectTypeDAO() {
		super("object_type", ObjectType.class,true);
	}

}
