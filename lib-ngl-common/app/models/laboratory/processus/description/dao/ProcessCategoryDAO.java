package models.laboratory.processus.description.dao;

import models.laboratory.common.description.dao.AbstractCategoryDAO;
import models.laboratory.processus.description.ProcessCategory;

import org.springframework.stereotype.Repository;

@Repository
public class ProcessCategoryDAO extends AbstractCategoryDAO<ProcessCategory>{

	public ProcessCategoryDAO() {
		super("process_category",ProcessCategory.class);
	}

}
