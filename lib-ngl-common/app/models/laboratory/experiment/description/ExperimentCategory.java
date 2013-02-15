package models.laboratory.experiment.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.experiment.description.dao.ExperimentCategoryDAO;

public class ExperimentCategory extends AbstractCategory{

	public ExperimentCategory() {
		super(ExperimentCategoryDAO.class.getName());
	}

	public static Finder<ExperimentCategory> find = new Finder<ExperimentCategory>(ExperimentCategoryDAO.class.getName()); 
}
