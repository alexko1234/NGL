package models.laboratory.project.description.dao;

import models.laboratory.common.description.dao.AbstractCategoryDAO;
import models.laboratory.project.description.ProjectCategory;

import org.springframework.stereotype.Repository;

@Repository
public class ProjectCategoryDAO extends AbstractCategoryDAO<ProjectCategory>{

	public ProjectCategoryDAO() {
		super("project_category",ProjectCategory.class);
	}
}
