package models.laboratory.experiment.description.dao;

import models.laboratory.experiment.description.ExperimentCategory;
import models.utils.dao.AbstractDAODefault;

import org.springframework.stereotype.Repository;

@Repository
public class ExperimentCategoryDAO extends AbstractDAODefault<ExperimentCategory>{

	public ExperimentCategoryDAO() {
		super("experiment_category",ExperimentCategory.class,true);
	}

}
