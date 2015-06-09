package models.laboratory.run.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.run.description.dao.RunCategoryDAO;


public class RunCategory extends AbstractCategory<RunCategory> {
	public static Finder<RunCategory> find = new Finder<RunCategory>(RunCategoryDAO.class.getName());
	
	public RunCategory() {
		super(RunCategoryDAO.class.getName());
	}
}
