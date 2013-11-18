package models.laboratory.common.description.dao;

import models.laboratory.common.description.StateCategory;
import models.utils.dao.AbstractDAODefault;

import org.springframework.stereotype.Repository;

@Repository
public class StateCategoryDAO extends AbstractDAODefault<StateCategory>{

	protected StateCategoryDAO() {
		super("state_category", StateCategory.class, true);
	}

	

	
}
