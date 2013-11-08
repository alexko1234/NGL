package models.laboratory.project.description.dao;

import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.utils.DescriptionHelper;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.stereotype.Repository;

import play.api.modules.spring.Spring;

@Repository
public class ProjectTypeDAO extends AbstractDAOMapping<ProjectType>{

	protected ProjectTypeDAO() {
		super("project_type", ProjectType.class, ProjectTypeMappingQuery.class, 
				"SELECT t.id, t.fk_common_info_type, t.fk_project_category "+
						"FROM project_type as t "+
						"JOIN common_info_type as c ON c.id=fk_common_info_type ", false);
	}

	@Override
	public long save(ProjectType projectType) throws DAOException
	{
		
		if(null == projectType){
			throw new DAOException("ProjectType is mandatory");
		}
		//Check if category exist
		if(projectType.category == null || projectType.category.id == null){
			throw new DAOException("ProjectCategory is not present !!");
		}
		
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		projectType.id = commonInfoTypeDAO.save(projectType);
		
		//Create new projectType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", projectType.id);
		parameters.put("fk_common_info_type", projectType.id);
		parameters.put("fk_project_category", projectType.category.id);
		jdbcInsert.execute(parameters);
		return projectType.id;
	}

	@Override
	public void update(ProjectType projectType) throws DAOException
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(projectType);
	}

	@Override
	public void remove(ProjectType projectType) throws DAOException {
		//Remove ProjectType
		super.remove(projectType);
		//Remove commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(projectType);
	}
}
