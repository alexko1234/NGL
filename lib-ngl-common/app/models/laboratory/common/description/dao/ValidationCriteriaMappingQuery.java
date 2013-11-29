package models.laboratory.common.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.common.description.ValidationCriteria;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

public class ValidationCriteriaMappingQuery extends MappingSqlQuery<ValidationCriteria>{

	public ValidationCriteriaMappingQuery()
	{
		super();
	}
	
	public ValidationCriteriaMappingQuery(DataSource ds, String sql,SqlParameter sqlParameter)
	{
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	
	@Override
	protected ValidationCriteria mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		ValidationCriteria validationCriteria = new ValidationCriteria();
		validationCriteria.id=rs.getLong("id");
		validationCriteria.code=rs.getString("code");
		validationCriteria.name=rs.getString("name");

		return validationCriteria;
	}

}