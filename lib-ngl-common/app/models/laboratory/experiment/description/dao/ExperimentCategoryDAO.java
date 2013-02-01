package models.laboratory.experiment.description.dao;

import models.laboratory.common.description.dao.AbstractCategoryDAO;
import models.laboratory.experiment.description.ExperimentCategory;

import org.springframework.stereotype.Repository;

@Repository
public class ExperimentCategoryDAO extends AbstractCategoryDAO<ExperimentCategory>{

	public ExperimentCategoryDAO() {
		super("experiment_category",ExperimentCategory.class);
	}
	
}
