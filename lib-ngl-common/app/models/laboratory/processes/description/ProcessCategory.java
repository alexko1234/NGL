package models.laboratory.processes.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.processes.description.dao.ProcessCategoryDAO;
//TODO: fix doc generation that produces an error with the unqualified name
import models.utils.Model.Finder;
import models.utils.dao.AbstractDAO;

public class ProcessCategory extends AbstractCategory<ProcessCategory> {

//	public static Finder<ProcessCategory> find = new Finder<ProcessCategory>(ProcessCategoryDAO.class.getName());
	public static final Finder<ProcessCategory,ProcessCategoryDAO> find = new Finder<>(ProcessCategoryDAO.class);
	
	public ProcessCategory() {
		super(ProcessCategoryDAO.class.getName());
	}

	@Override
	protected Class<? extends AbstractDAO<ProcessCategory>> daoClass() {
		return ProcessCategoryDAO.class;
	}

}
