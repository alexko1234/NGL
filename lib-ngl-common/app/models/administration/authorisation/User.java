package models.administration.authorisation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.cea.ig.authentication.html.IAuthenticate;
import models.administration.authorisation.description.dao.UserDAO;
import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.dao.InstituteDAO;
import models.laboratory.common.description.dao.PropertyDefinitionDAO;
import models.laboratory.common.description.dao.StateDAO;
import models.utils.Model;
import models.utils.Model.Finder;
import models.utils.dao.DAOException;
import play.data.validation.ValidationError;


public class User extends Model<User>{
		
		public User() {
			super(UserDAO.class.getName());
		}
	
		public String login;
		
		public String firstname;
		
		public String lastname;
		
		
		public String email;
		
		/**
		 * 0 not technical, 1 technical
		 */
		public int technicaluser;
		
		/**
		 * Only for technical users
		 */
		public String password;
		
		public String confirmpassword;
		public List<Role> roles;
		public List<Team> teams;
		public List<Application> applications;
		
		@JsonIgnore
		public static UserFinder find = new UserFinder();
		@JsonIgnore
		public  Map<String,List<ValidationError>> validate() {
		        if(!password.equals(confirmpassword)) {
		        	Map<String,List<ValidationError>> map = new HashMap<String,List<ValidationError>>();
		        	List<ValidationError> listeValidation = new ArrayList<ValidationError>();
		        	listeValidation.add(new ValidationError("password","Password and confirmation are not the same.",null));
		        	map.put("password",listeValidation);
		        	map.put("confirmpassword",listeValidation);
		            return map;
		        }
		        return null;
		}
		
		
		public static class UserFinder extends Finder<User>{

			public UserFinder() {
				super(UserDAO.class.getName());
				 //super(StateDAO.class.getName());
			}
			
			public User findByLogin(String login) throws DAOException{
				return ((UserDAO)getInstance()).findByLogin(login);
			}
			
			
			
		}
}
