package models.laboratory.common.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.resolutions.description.Resolution;
import models.laboratory.resolutions.description.ResolutionCategory;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

public class ResolutionMappingQuery extends MappingSqlQuery<Resolution>{

	public ResolutionMappingQuery()
	{
		super();
	}
	public ResolutionMappingQuery(DataSource ds, String sql,SqlParameter sqlParameter)
	{
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	
	@Override
	protected Resolution mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		Resolution resolution = new Resolution();
		resolution.id=rs.getLong("id");
		resolution.code=rs.getString("code");
		resolution.name=rs.getString("name");
		
		long idCategory = rs.getLong("fk_resolution_category");
		ResolutionCategory category = null;
		try {
			category = ResolutionCategory.find.findById(idCategory);
		} catch (DAOException e) {
			throw new SQLException(e);
		}
		resolution.category=category;
		return resolution;
	}

}
