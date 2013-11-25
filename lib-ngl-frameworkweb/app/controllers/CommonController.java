package controllers;

import org.codehaus.jackson.JsonNode;

import controllers.history.UserHistory;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.With;
import play.mvc.Http.Context;

@With(UserHistory.class)
public abstract class CommonController extends Controller{
	
	/**
	 * Fill a form in json mode
	 * @param form
	 * @param clazz
	 * @return
	 */
	protected static <T> Form<T> getFilledForm(Form<T> form, Class<T> clazz) {		
		JsonNode json = request().body().asJson();
		T input = Json.fromJson(json, clazz);
		Form<T> filledForm = form.fill(input); 
		return filledForm;
	}

	public static String getCurrentUser(){
		String user = Context.current().session().get("NGL_FILTER_USER");
		if(null == user)user = "ngsrg";
		return user;
	}

}
