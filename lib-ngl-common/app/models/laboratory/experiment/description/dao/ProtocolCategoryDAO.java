package models.laboratory.experiment.description.dao;

import models.laboratory.common.description.dao.AbstractCategoryDAO;
import models.laboratory.experiment.description.ProtocolCategory;

import org.springframework.stereotype.Repository;

@Repository
public class ProtocolCategoryDAO extends AbstractCategoryDAO<ProtocolCategory>{

	public ProtocolCategoryDAO() {
		super("protocol_category",ProtocolCategory.class);
	}

	
}
