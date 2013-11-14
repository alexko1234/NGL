package models.administration.authorisation.description.dao;

import java.util.List;

import models.administration.authorisation.User;
import models.utils.dao.AbstractDAO;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import play.Logger;
import play.modules.html.IAuthenticate;

@Repository
public class UserDAO extends AbstractDAO<User> implements IAuthenticate{ 

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
	
}
