package models.laboratory.project.description.dao;

import models.laboratory.project.description.ProjectCategory;
import models.utils.dao.AbstractDAO;

import org.springframework.stereotype.Repository;

@Repository
public class ProjectCategoryDAO extends AbstractDAO<ProjectCategory>{

	public ProjectCategoryDAO() {
		super("project_category",ProjectCategory.class,true);
	}
}
