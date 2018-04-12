package controllers.commons.api;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import controllers.APICommonController;
//import controllers.CommonController;
import controllers.authorisation.Permission;
import fr.cea.ig.play.NGLContext;
import models.administration.authorisation.User;
import models.administration.authorisation.description.dao.UserDAO;
import models.utils.ListObject;
import models.utils.dao.DAOException;
//import play.Logger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;

public class Users extends APICommonController<UserSearchForm> { //CommonController{

	private static final play.Logger.ALogger logger = play.Logger.of(Users.class);
	
	//	private final /*static*/ Form<UserSearchForm> userSearchForm; // = form(UserSearchForm.class);
	private final /*static*/ Form<User> userForm; // = form(User.class);
	
	@Inject
	public Users(NGLContext ctx) {
		super(ctx, UserSearchForm.class);
//		this.userSearchForm = ctx.form(UserSearchForm.class);
		this.userForm = ctx.form(User.class);
	}
	
	/*
	 * Get Method
	 */
	public Result get(String login) throws DAOException{
		User user = User.find.findByLogin(login);
		if (user == null)
			return notFound(login);
		return ok(Json.toJson(user));
	}
	
	/*
	 * List Method
	 */
	@Permission(value={"reading"})
	public Result list() throws DAOException{
		UserSearchForm form = filledFormQueryString(UserSearchForm.class);
		try {
			List<User> users = new ArrayList<>();
			if (StringUtils.isNotBlank(form.login)) {
				users = User.find.findByLikeLogin(toLikeLogin(form.login));
			} else {
				users = User.find.findAll();
			}
			if (form.datatable) {
				return ok(Json.toJson(new DatatableResponse<>(users, users.size()))); 
			} else if(form.list) {
				List<ListObject> lop = new ArrayList<>();
				for(User et : users) {
					lop.add(new ListObject(et.login, et.login));
				}
				return Results.ok(Json.toJson(lop));
			} else {
				return Results.ok(Json.toJson(users));
			}
		} catch (DAOException e) {
			logger.error("DAO error: " + e.getMessage());
			return  Results.internalServerError(e.getMessage());
		}
	}
	
	/*
	 * rolesUpdate()		>( PUT )
	 */
	@Permission(value={"admin"})
	public Result update(String userLogin) throws DAOException{
		User user = getUsers(userLogin);
		if (user == null)
			return badRequest("User with login "+userLogin+" does not exist");
		
		Form<User> filledForm = getFilledForm(userForm, User.class);
		User userInput = filledForm.get();
		
		if (userInput.login.equals(userLogin)) {
			try {
				UserDAO dao = Spring.getBeanOfType(UserDAO.class);
				dao.insertUserRoles(userInput.id, userInput.roleIds, true);
			} catch (DAOException e) {
				logger.error("DAO error: " + e.getMessage());
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
	private String toLikeLogin(String aLogin){
		return "%" + aLogin + "%";	
	}
	
	/*
	 * Method to getUsers
	 * 		used in update()..
	 */
	private User getUsers(String aLogin) throws DAOException{
		User user = User.find.findByLogin(aLogin);
		return user;
	}
	
}
