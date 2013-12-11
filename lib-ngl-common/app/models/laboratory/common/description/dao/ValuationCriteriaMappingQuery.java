package models.laboratory.common.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.common.description.ValuationCriteria;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

public class ValuationCriteriaMappingQuery extends MappingSqlQuery<ValuationCriteria>{

	public ValuationCriteriaMappingQuery() {
		super();
	}
	
	public ValuationCriteriaMappingQuery(DataSource ds, String sql,SqlParameter sqlParameter) {
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	
	@Override
	protected ValuationCriteria mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		ValuationCriteria valuationCriteria = new ValuationCriteria();
		valuationCriteria.id=rs.getLong("id");
		valuationCriteria.code=rs.getString("code");
		valuationCriteria.name=rs.getString("name");

		return valuationCriteria;
	}

}