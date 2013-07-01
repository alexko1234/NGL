package models.laboratory.processes.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.processes.description.dao.ProcessCategoryDAO;


public class ProcessCategory extends AbstractCategory<ProcessCategory>{

	public static Finder<ProcessCategory> find = new Finder<ProcessCategory>(ProcessCategoryDAO.class.getName());
	
	public ProcessCategory() {
		super(ProcessCategoryDAO.class.getName());
	}

}
