package models.laboratory.common.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.common.description.Valuation;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

public class ValuationMappingQuery extends MappingSqlQuery<Valuation>{

	public ValuationMappingQuery() {
		super();
	}
	
	public ValuationMappingQuery(DataSource ds, String sql,SqlParameter sqlParameter) {
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	
	@Override
	protected Valuation mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		Valuation valuation = new Valuation();
		valuation.id=rs.getLong("id");
		valuation.code=rs.getString("code");
		valuation.name=rs.getString("name");

		return valuation;
	}

}