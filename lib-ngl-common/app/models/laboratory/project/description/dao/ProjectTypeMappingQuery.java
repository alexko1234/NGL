package models.laboratory.project.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.modules.spring.Spring;

import com.avaje.ebean.enhance.asm.Type;

public class ProjectTypeMappingQuery extends MappingSqlQuery<ProjectType>{

	public ProjectTypeMappingQuery(DataSource ds)
	{
		super(ds,"SELECT id, fk_common_info_type, fk_project_category "+
				"FROM project_type "+
				"WHERE id = ? ");
		super.declareParameter(new SqlParameter("id", Type.LONG));
		compile();
	}
	
	@Override
	protected ProjectType mapRow(ResultSet rs, int rowNum) throws SQLException {
		ProjectType projectType = new ProjectType();
		projectType.id = rs.getLong("id");
		long idCommonInfoType = rs.getLong("fk_common_info_type");
		long idProjectCategory = rs.getLong("fk_project_category");
		//Get commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType commonInfoType = commonInfoTypeDAO.findById(idCommonInfoType);
		projectType.setCommonInfoType(commonInfoType);
		//Get category
		ProjectCategoryDAO projectCategoryDAO = Spring.getBeanOfType(ProjectCategoryDAO.class);
		ProjectCategory projectCategory = projectCategoryDAO.findById(idProjectCategory);
		projectType.projectCategory = projectCategory;
		return projectType;
	}

}
