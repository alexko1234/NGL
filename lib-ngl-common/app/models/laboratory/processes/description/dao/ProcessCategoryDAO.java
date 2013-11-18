package models.laboratory.processes.description.dao;

import models.laboratory.processes.description.ProcessCategory;
import models.utils.dao.AbstractDAODefault;

import org.springframework.stereotype.Repository;

@Repository
public class ProcessCategoryDAO extends AbstractDAODefault<ProcessCategory>{

	public ProcessCategoryDAO() {
		super("process_category",ProcessCategory.class,true);
	}

}
