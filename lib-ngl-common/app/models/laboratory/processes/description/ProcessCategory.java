package models.laboratory.processes.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.processes.description.dao.ProcessCategoryDAO;
//TODO: fix doc generation that produces an error with the unqualified name
import models.utils.Model.Finder;

public class ProcessCategory extends AbstractCategory<ProcessCategory> {

//	public static Finder<ProcessCategory> find = new Finder<ProcessCategory>(ProcessCategoryDAO.class.getName());
	public static Finder<ProcessCategory> find = new Finder<ProcessCategory>(ProcessCategoryDAO.class);
	
	public ProcessCategory() {
		super(ProcessCategoryDAO.class.getName());
	}

}
