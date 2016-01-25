package models.administration.authorisation.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import models.administration.authorisation.Role;
import models.administration.authorisation.User;
import models.utils.dao.DAOException;
import play.Logger;

/**
 * 
 * @author michieli
 *
 */
public class UserMappingQuery extends MappingSqlQuery<User>{

	public UserMappingQuery(){
		super();
	}
	
	public UserMappingQuery(DataSource ds, String sql, SqlParameter sqlParameter){
		super(ds,sql);
		if(sqlParameter != null)
			super.declareParameter(sqlParameter);
		compile();
	}

	@Override
	protected User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = new User();
		user.id = rs.getLong("id");
		user.login = rs.getString("login");
		user.firstname = rs.getString("firstname");
		user.lastname = rs.getString("lastname");
		user.email = rs.getString("email");
		user.technicaluser = rs.getInt("technicaluser");
		
		List<Long> someIds = new ArrayList();
		List<String> someLabels = new ArrayList();
		try {
			List<Role> roles = Role.find.findByUserLogin(user.login);
			for(Role r:roles){
				someIds.add(r.id);
			}
			user.roleIds = someIds;
		} catch (DAOException e) {
			throw new SQLException(e);
		}
		
		return user;
	}
}
