package controllers.manips.api;

import static play.data.Form.form;

import java.util.List;

import ls.models.Manip;
import ls.services.LimsManipServices;
import play.Logger;
import play.api.modules.spring.Spring;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;

public class Manips {

	final static Form<Manip> runForm = form(Manip.class);
	final static DynamicForm listForm = new DynamicForm();

	public static Result list(){
		DynamicForm filledForm =  listForm.bindFromRequest();
		LimsManipServices  limsManipServices = Spring.getBeanOfType(LimsManipServices.class);
		List<Manip> manips = limsManipServices.getManips(getEtmanipValue(),2,"");
		System.err.println("Manips nb "+manips.size());
		System.err.println("Etmanip "+getEtmanipValue());
		return play.mvc.Results.ok(Json.toJson(new DatatableResponse(manips, manips.size())));
	}


	private static String getProjetValue() {
		try{
			return play.mvc.Controller.request().queryString().get("projet")[0];
		}catch(Exception e){
			Logger.error(e.getMessage());
			return "null"; // default value;
		}
	}

	private static Integer getEtmanipValue() {
		try{
			return Integer.valueOf(play.mvc.Controller.request().queryString().get("etmanip")[0]);
		}catch(Exception e){
			Logger.error(e.getMessage());
			return 2; // default value;
		}
	}
}
