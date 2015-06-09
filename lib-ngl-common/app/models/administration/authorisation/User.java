package models.administration.authorisation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.data.validation.Constraints.Required;
import play.data.validation.ValidationError;
import play.db.ebean.Model;

import com.avaje.ebean.Page;

@Entity
public class User extends Model{
		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		public int id;
	
		@Required
		public String login;
		
		public String firstname;
		
		public String lastname;
		
		@Required
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
		
		@ManyToMany(cascade={CascadeType.ALL})
		public List<Role> roles;
		
		@ManyToMany(cascade={CascadeType.ALL})
		public List<Team> teams;
		
		@ManyToMany(cascade={CascadeType.ALL})
		public List<Application> applications;
		
		public static Finder<Integer,User> find = new Finder<Integer,User>(Integer.class, User.class);
		
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
		
		public static Page<User> page(int page, int pageSize, String sortBy, String order, String firstname,  String lastname ,String login,String mail) {
			return 
					find.where()
					.ilike("firstname", "%" + firstname + "%")
					.ilike("login",  "%" + login + "%")
					.ilike("lastname",  "%" + lastname + "%")
					.ilike("email",  "%" + mail + "%")
					.orderBy(sortBy + " " + order)
					.findPagingList(pageSize)
					.getPage(page);
		}
}
