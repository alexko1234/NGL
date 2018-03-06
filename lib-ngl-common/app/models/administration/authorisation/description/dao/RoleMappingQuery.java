package models.administration.authorisation.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import models.administration.authorisation.Permission;
import models.administration.authorisation.Role;
import models.utils.dao.DAOException;
import models.utils.dao.MappingSqlQueryFactory;

/**
 * 
 * @author michieli
 *
 */
public class RoleMappingQuery extends MappingSqlQuery<Role>{

	public static final MappingSqlQueryFactory<Role> factory = (d,s) -> new RoleMappingQuery(d,s,null);
	
//	public RoleMappingQuery(){
//		super();
//	}
	
	public RoleMappingQuery(DataSource ds, String sql, SqlParameter sqlParameter) {
		super(ds,sql);
		if (sqlParameter != null)
			super.declareParameter(sqlParameter);
		compile();
	}

	@Override
	protected Role mapRow(ResultSet rs, int rowNum) throws SQLException {
		Role role = new Role();
		role.id    = rs.getLong("id");
		role.label = rs.getString("label");
		return role;
	}

}
