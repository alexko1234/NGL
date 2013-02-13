package models.laboratory.processus.description.dao;

import models.laboratory.processus.description.ProcessCategory;
import models.utils.dao.AbstractDAO;

import org.springframework.stereotype.Repository;

@Repository
public class ProcessCategoryDAO extends AbstractDAO<ProcessCategory>{

	public ProcessCategoryDAO() {
		super("process_category",ProcessCategory.class,true);
	}

}
