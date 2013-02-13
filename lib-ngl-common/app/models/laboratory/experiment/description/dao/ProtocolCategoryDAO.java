package models.laboratory.experiment.description.dao;

import models.laboratory.experiment.description.ProtocolCategory;
import models.utils.dao.AbstractDAO;

import org.springframework.stereotype.Repository;

@Repository
public class ProtocolCategoryDAO extends AbstractDAO<ProtocolCategory>{

	public ProtocolCategoryDAO() {
		super("protocol_category",ProtocolCategory.class,true);
	}

	
}
