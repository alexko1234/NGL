package models.administration.authorisation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.administration.authorisation.description.dao.UserDAO;
import models.utils.Model;
import models.utils.dao.DAOException;
//import play.Logger;
import play.data.validation.ValidationError;


public class User extends Model<User> {

	@JsonIgnore
	public static final UserFinder find = new UserFinder();

	public String login;
	public String firstname;
	public String lastname;
	public String email;

	/**
	 * 0 not technical, 1 technical
	 */
	// Looks like a boolean
	public int technicaluser;

	/**
	 * Only for technical users
	 */
	public String password;
	public String confirmpassword;
	
	// Attribute used to Lists the roles labels
	public List<Long> roleIds;
	public List<Team> teams;
	public List<Application> applications;
	public Boolean active;

	public User() {
		super(UserDAO.class.getName());
	}

	@JsonIgnore
	public  Map<String,List<ValidationError>> validate() {
		if (!password.equals(confirmpassword)) {
			Map<String,List<ValidationError>> map = new HashMap<String,List<ValidationError>>();
			List<ValidationError> listeValidation = new ArrayList<ValidationError>();
			listeValidation.add(new ValidationError("password","Password and confirmation are not the same.",null));
			map.put("password",listeValidation);
			map.put("confirmpassword",listeValidation);
			return map;
		}
		return null;
	}

	/**
	 * 
	 * @author michieli
	 *	
	 */
	
	// Doc generation produces an error with the parent unqualified name.
	// public static class UserFinder extends Finder<User> {
	public static class UserFinder extends Model.Finder<User,UserDAO> {

//		public UserFinder() {
//			super(UserDAO.class.getName());
//		}
		public UserFinder() { super(UserDAO.class); }

		public List<User> findAll() throws DAOException {
//			return ((UserDAO)getInstance()).findAll();
			return getInstance().findAll();
		}

		public User findByLogin(String login) throws DAOException {			
//			return ((UserDAO)getInstance()).findByLogin(login);
			return getInstance().findByLogin(login);
		}

		public List<User> findByLikeLogin(String aLike) throws DAOException {
//			return ((UserDAO)getInstance()).findByLikeLogin(aLike);
			return getInstance().findByLikeLogin(aLike);
		}
		
	}
	
}
