package models.laboratory.common.description.dao;

import models.laboratory.common.description.ResolutionCategory;
import models.utils.dao.AbstractDAODefault;

import org.springframework.stereotype.Repository;

@Repository
public class ResolutionCategoryDAO extends AbstractDAODefault<ResolutionCategory>{

	protected ResolutionCategoryDAO() {
		super("resolution_category", ResolutionCategory.class, true);
	}

	

	
}
