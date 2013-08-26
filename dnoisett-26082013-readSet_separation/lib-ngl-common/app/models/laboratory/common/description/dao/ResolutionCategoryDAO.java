package models.laboratory.common.description.dao;

import models.laboratory.common.description.ResolutionCategory;
import models.utils.dao.AbstractDAO;

import org.springframework.stereotype.Repository;

@Repository
public class ResolutionCategoryDAO extends AbstractDAO<ResolutionCategory>{

	protected ResolutionCategoryDAO() {
		super("resolution_category", ResolutionCategory.class, true);
	}

	

	
}
