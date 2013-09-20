package models.laboratory.run.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.run.description.TreatmentContext;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;


public class TreatmentContextMappingQuery extends MappingSqlQuery<TreatmentContext>{

	public TreatmentContextMappingQuery()
	{
		super();
	}
	public TreatmentContextMappingQuery(DataSource ds, String sql, SqlParameter sqlParameter)
	{
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	
	@Override
	protected TreatmentContext mapRow(ResultSet rs, int rowNum) throws SQLException {
		TreatmentContext treatmentContext = new TreatmentContext();
			
		treatmentContext.id = rs.getLong("id");
		treatmentContext.code = rs.getString("code");  
		treatmentContext.name = rs.getString("name");  
			
		return treatmentContext;

	}

}


