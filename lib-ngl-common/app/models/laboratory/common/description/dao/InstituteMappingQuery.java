package models.laboratory.common.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.common.description.Institute;
import models.utils.dao.MappingSqlQueryFactory;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

public class InstituteMappingQuery extends MappingSqlQuery<Institute> {

	public static final MappingSqlQueryFactory<Institute> factory = (d,s) -> new InstituteMappingQuery(d,s,null);
	
//	public InstituteMappingQuery() {
//		super();
//	}
	
	public InstituteMappingQuery(DataSource ds, String sql,SqlParameter sqlParameter) {
		super(ds,sql);
		if (sqlParameter != null)
//			super.declareParameter(sqlParameter);
			declareParameter(sqlParameter);
		compile();
	}
	
	@Override
	protected Institute mapRow(ResultSet rs, int rowNum) throws SQLException {
		Institute institute = new Institute();
		institute.id   = rs.getLong("id");
		institute.code = rs.getString("code");
		institute.name = rs.getString("name");
		return institute;
	}

}
