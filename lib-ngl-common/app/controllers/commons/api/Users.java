package controllers.commons.api;

// import static play.data.Form.form;
import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import controllers.CommonController;
import controllers.authorisation.Permission;
import models.administration.authorisation.User;
import models.administration.authorisation.description.dao.UserDAO;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.Logger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;

public class Users extends CommonController{
	final static Form<UserSearchForm> userSearchForm = form(UserSearchForm.class);
	final static Form<User> userForm = form(User.class);
	/*
	 * Get Method
	 */
	public static Result get(String login) throws DAOException{
		User user = User.find.findByLogin(login);
		if(user == null){
			return notFound(login);
		}
		return ok(Json.toJson(user));
	}
	
	/*
	 * List Method
	 */
	@Permission(value={"reading"})
	public static Result list() throws DAOException{
		UserSearchForm form = filledFormQueryString(UserSearchForm.class);

		try{
			List<User> users = new ArrayList<User>();
			if(StringUtils.isNotBlank(form.login)){
				users = User.find.findByLikeLogin(toLikeLogin(form.login));
			}else{
				users = User.find.findAll();
			}
			
			if(form.datatable){
				return ok(Json.toJson(new DatatableResponse<User>(users, users.size()))); 
			}else if(form.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(User et:users){
					lop.add(new ListObject(et.login, et.login));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(users));
			}
		
		} catch (DAOException e) {
			Logger.error("DAO error: " + e.getMessage());
			return  Results.internalServerError(e.getMessage());
		}
	}
	
	/*
	 * rolesUpdate()		>( PUT )
	 */
	@Permission(value={"admin"})
	public static Result update(String userLogin) throws DAOException{
		User user = getUsers(userLogin);
		if(user == null)
			return badRequest("User with login "+userLogin+" does not exist");
		
		Form<User> filledForm = getFilledForm(userForm, User.class);
		User userInput = filledForm.get();
		
		if(userInput.login.equals(userLogin)){
			try{
				UserDAO dao = Spring.getBeanOfType(UserDAO.class);
				dao.insertUserRoles(userInput.id, userInput.roleIds, true);
			} catch (DAOException e){
				Logger.error("DAO error: " + e.getMessage());
				return  Results.internalServerError(e.getMessage());
			}
		} else {
			return badRequest("Not the same login !");
		}			
		return Results.ok();
	}
	
	/*
	 * toLikeLogin
	 * Change a String into %String%
	 */
	private static String toLikeLogin(String aLogin){
		return "%" + aLogin + "%";	
	}
	
	/*
	 * Method to getUsers
	 * 		used in update()..
	 */
	private static User getUsers(String aLogin) throws DAOException{
		User user = User.find.findByLogin(aLogin);
		return user;
	}
}
