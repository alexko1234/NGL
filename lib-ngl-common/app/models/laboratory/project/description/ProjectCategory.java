package models.laboratory.project.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.project.description.dao.ProjectCategoryDAO;
import models.utils.dao.AbstractDAO;

public class ProjectCategory extends AbstractCategory<ProjectCategory>{

//	public static Finder<ProjectCategory> find = new Finder<ProjectCategory>(ProjectCategoryDAO.class.getName());
	public static final Finder<ProjectCategory,ProjectCategoryDAO> find = new Finder<>(ProjectCategoryDAO.class);
	
	public ProjectCategory() {
		super(ProjectCategoryDAO.class.getName());
	}

	@Override
	protected Class<? extends AbstractDAO<ProjectCategory>> daoClass() {
		return ProjectCategoryDAO.class;
	}

}
