package controllers;


import com.fasterxml.jackson.databind.JsonNode;

import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.With;
import play.mvc.Http.Context;

@With({fr.cea.ig.authentication.Authenticate.class})
public abstract class TPLCommonController extends Controller {
	/**
	 * Fill a form in json mode
	 * @param form
	 * @param clazz
	 * @return
	 */
	protected <P> Form<P> getFilledForm(Form<P> form, Class<P> clazz) {		
		JsonNode json = request().body().asJson();
		P input = Json.fromJson(json, clazz);
		Form<P> filledForm = form.fill(input); 
		return filledForm;
	}
	
	protected String getCurrentUser(){
		return Context.current().request().username();
	}
}
