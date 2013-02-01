package models.laboratory.project.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

import com.avaje.ebean.enhance.asm.Type;

@Repository
public class ProjectTypeDAO {

	private SimpleJdbcInsert jdbcInsert;
	private DataSource dataSource;
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("project_type");
	}

	public ProjectType findById(long id)
	{
		String sql = "SELECT id, fk_common_info_type, fk_project_category "+
				"FROM project_type "+
				"WHERE id = ? ";
		ProjectTypeMappingQuery projectTypeMappingQuery = new ProjectTypeMappingQuery(dataSource,sql,new SqlParameter("id", Type.LONG));
		return projectTypeMappingQuery.findObject(id);
	}
	
	public ProjectType findByCode(String code)
	{
		String sql = "SELECT pt.id, fk_common_info_type, fk_project_category "+
				"FROM project_type as pt "+
				"JOIN common_info_type as c ON c.id=fk_common_info_type "+
				"WHERE code = ? ";
		ProjectTypeMappingQuery projectTypeMappingQuery=new ProjectTypeMappingQuery(dataSource,sql, new SqlParameter("code", Types.VARCHAR));
		return projectTypeMappingQuery.findObject(code);
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
			ProjectCategory pc = (ProjectCategory) projectCategoryDAO.add(projectType.projectCategory);
			projectType.projectCategory = pc;
		}
		//Create new projectType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", projectType.id);
		parameters.put("fk_common_info_type", projectType.id);
		parameters.put("fk_project_category", projectType.projectCategory.id);
		jdbcInsert.execute(parameters);
		return projectType;
	}

	public void update(ProjectType projectType)
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(projectType);
	}
}
