package models.laboratory.project.description.dao;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class ProjectTypeDAO {

	private ProjectTypeMappingQuery projectTypeMappingQuery;
	private SimpleJdbcInsert jdbcInsert;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.projectTypeMappingQuery = new ProjectTypeMappingQuery(dataSource);
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("project_type").usingGeneratedKeyColumns("id");
	}

	public ProjectType findById(long id)
	{
		return this.projectTypeMappingQuery.findObject(id);
	}

	public ProjectType add(ProjectType projectType)
	{
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType cit = commonInfoTypeDAO.add(projectType);
		projectType.setCommonInfoType(cit);
		//Check if category exist
		if(projectType.projectCategory!=null && projectType.projectCategory.id==null)
		{
			ProjectCategoryDAO projectCategoryDAO = Spring.getBeanOfType(ProjectCategoryDAO.class);
			ProjectCategory pc = projectCategoryDAO.add(projectType.projectCategory);
			projectType.projectCategory = pc;
		}
		//Create new projectType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fk_common_info_type", projectType.getIdCommonInfoType());
		parameters.put("fk_project_category", projectType.projectCategory.id);
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		projectType.id = newId;
		return projectType;
	}

	public void update(ProjectType projectType)
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(projectType);
	}
}
