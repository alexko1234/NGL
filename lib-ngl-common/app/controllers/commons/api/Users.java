package controllers.commons.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import models.administration.authorisation.User;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

public class Users extends CommonController{
	final static Form<UserSearchFrom> userForm = form(UserSearchFrom.class);
	
	public static Result get(String login) throws DAOException{
		User user = User.find.findByLogin(login);
		if(user == null){
			return notFound(login);
		}
		
		return ok(Json.toJson(user));
	}
	
	public static Result list() throws DAOException{
		Form<UserSearchFrom> userFilledForm = filledFormQueryString(userForm,UserSearchFrom.class);
		UserSearchFrom usersSearch = userFilledForm.get();

		List<User> users;
		
		if(CollectionUtils.isNotEmpty(usersSearch.logins)){
			//Pas priori pas utilis
			//users =  User.find.where(Expr.in("login", usersSearch.logins)).findList();
			users = User.find.findAll();
		}else{
			users = User.find.findAll();
		}
		
		if(usersSearch.datatable){
			return ok(Json.toJson(new DatatableResponse<User>(users, users.size()))); 
		}else if(usersSearch.list){
			List<ListObject> lop = new ArrayList<ListObject>();
			for(User et:users){
				lop.add(new ListObject(et.login, et.login));
			}
			return Results.ok(Json.toJson(lop));
		}else{
			return Results.ok(Json.toJson(users));
		}
	}
}
