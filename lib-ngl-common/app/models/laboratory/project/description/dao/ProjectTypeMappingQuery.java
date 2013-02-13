package models.laboratory.project.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.modules.spring.Spring;

public class ProjectTypeMappingQuery extends MappingSqlQuery<ProjectType>{

	public ProjectTypeMappingQuery()
	{
		super();
	}
	public ProjectTypeMappingQuery(DataSource ds, String sql, SqlParameter sqlParameter)
	{
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	
	@Override
	protected ProjectType mapRow(ResultSet rs, int rowNum) throws SQLException {
		try {
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
			ProjectCategory projectCategory=null;
			try {
				projectCategory = (ProjectCategory) projectCategoryDAO.findById(idProjectCategory);
			} catch (DAOException e) {
				throw new SQLException(e);
			}
			projectType.projectCategory = projectCategory;
			return projectType;
		} catch (DAOException e) {
			throw new SQLException(e);
		}
	}

}
