package models.laboratory.processus.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.processus.description.dao.ProcessCategoryDAO;


public class ProcessCategory extends AbstractCategory{

	public static Finder<ProcessCategory> find = new Finder<ProcessCategory>(ProcessCategoryDAO.class.getName());
	
	public ProcessCategory() {
		super(ProcessCategoryDAO.class.getName());
	}

}
