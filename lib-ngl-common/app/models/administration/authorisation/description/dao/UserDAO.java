package models.administration.authorisation.description.dao;

import java.util.List;

import models.administration.authorisation.Role;
import models.administration.authorisation.User;
import models.utils.dao.AbstractDAODefault;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import fr.cea.ig.authentication.html.IAuthenticate;

@Repository
public class UserDAO extends AbstractDAODefault<User> implements IAuthenticate{ 

	protected UserDAO() {
		super("user", User.class, true);
	}
	
	public boolean isExistUserWithLoginAndPassword(String login, String password){
		String sql = "SELECT login "+
				"FROM user WHERE login='"+login+"' AND password='"+password+"'";
		
		BeanPropertyRowMapper<User> mapper = new BeanPropertyRowMapper<User>(User.class);
		List<User> us =  this.jdbcTemplate.query(sql, mapper);
		
		return this.jdbcTemplate.query(sql, mapper).size()>0;
	}

	public boolean isUserAccessApplication(String login, String application){
		String sql = "SELECT a.label "+
				"FROM user u, application a, user_application ua WHERE u.login='"+login+"' AND a.code='"+application+"' AND ua.user_id=u.id and ua.application_id=a.id";
		BeanPropertyRowMapper<User> mapper = new BeanPropertyRowMapper<User>(User.class);
		return this.jdbcTemplate.query(sql, mapper).size()>0;
	}
	
	public void setDefaultRole(String login, String role){
		String sql = "SELECT u.id FROM user u, user_role ur WHERE u.login=? and u.id=ur.user_id";
		
		BeanPropertyRowMapper<User> mapper = new BeanPropertyRowMapper<User>(User.class);
		if(this.jdbcTemplate.query(sql, mapper,login).size() == 0){
			sql = "INSERT INTO user_role (user_id,role_id) VALUE (?,?)";
			this.jdbcTemplate.update(sql, getUserId(login), getRoleId(role));
		}
	}
	
	private int getRoleId(String label){
		String sql = "SELECT r.id FROM role r WHERE r.label=?";
		BeanPropertyRowMapper<Role> mapper = new BeanPropertyRowMapper<Role>(Role.class);
		return this.jdbcTemplate.query(sql, mapper,label).get(0).id;
	}
	
	private int getUserId(String login){
		String sql = "SELECT u.id FROM user u WHERE u.login=?";
		BeanPropertyRowMapper<User> mapper = new BeanPropertyRowMapper<User>(User.class);
		return this.jdbcTemplate.query(sql, mapper,login).get(0).id;
	}
	

	
}
