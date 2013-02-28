package models.laboratory.experiment.description.dao;

import models.laboratory.experiment.description.ExperimentCategory;
import models.utils.dao.AbstractDAO;

import org.springframework.stereotype.Repository;

@Repository
public class ExperimentCategoryDAO extends AbstractDAO<ExperimentCategory>{

	public ExperimentCategoryDAO() {
		super("experiment_category",ExperimentCategory.class,true);
	}

}
