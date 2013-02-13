package models.laboratory.project.description.dao;

import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.project.description.ProjectType;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class ProjectTypeDAO extends AbstractDAOMapping<ProjectType>{

	protected ProjectTypeDAO() {
		super("project_type", ProjectType.class, ProjectTypeMappingQuery.class, 
				"SELECT t.id, fk_common_info_type, fk_project_category "+
				"FROM project_type as t "+
				"JOIN common_info_type as c ON c.id=fk_common_info_type ", false);
	}

	public long add(ProjectType projectType) throws DAOException
	{
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		projectType.id = commonInfoTypeDAO.add(projectType);
		//Check if category exist
		if(projectType.projectCategory!=null && projectType.projectCategory.id==null)
		{
			ProjectCategoryDAO projectCategoryDAO = Spring.getBeanOfType(ProjectCategoryDAO.class);
			projectType.projectCategory.id = projectCategoryDAO.add(projectType.projectCategory);
		}
		//Create new projectType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", projectType.id);
		parameters.put("fk_common_info_type", projectType.id);
		parameters.put("fk_project_category", projectType.projectCategory.id);
		jdbcInsert.execute(parameters);
		return projectType.id;
	}

	public void update(ProjectType projectType) throws DAOException
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(projectType);
	}



	@Override
	public void remove(ProjectType projectType) {
		//Remove commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(projectType);
		//Remove ProjectType
		super.remove(projectType);
	}
}
