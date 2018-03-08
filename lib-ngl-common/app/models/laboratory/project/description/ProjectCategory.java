package models.laboratory.project.description;

import models.laboratory.common.description.AbstractCategory;
// import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.project.description.dao.ProjectCategoryDAO;
//TODO: fix doc generation that produces an error with the unqualified name
import models.utils.Model.Finder;

public class ProjectCategory extends AbstractCategory<ProjectCategory>{

//	public static Finder<ProjectCategory> find = new Finder<ProjectCategory>(ProjectCategoryDAO.class.getName());
	public static Finder<ProjectCategory> find = new Finder<>(ProjectCategoryDAO.class);
	
	public ProjectCategory() {
		super(ProjectCategoryDAO.class.getName());
	}

}
