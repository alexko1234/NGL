package models.laboratory.project.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.project.description.dao.ProjectCategoryDAO;


public class ProjectCategory extends AbstractCategory<ProjectCategory>{

	public static Finder<ProjectCategory> find = new Finder<ProjectCategory>(ProjectCategoryDAO.class.getName());
	
	public ProjectCategory() {
		super(ProjectCategoryDAO.class.getName());
	}

}
