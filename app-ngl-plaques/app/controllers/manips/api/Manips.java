package controllers.manips.api;

import java.util.List;

import controllers.CommonController;

import ls.dao.LimsManipDAO;
import ls.models.Manip;
import ls.services.LimsManipServices;
import play.Logger;
import play.api.modules.spring.Spring;
import play.data.DynamicForm;
import play.libs.Json;

import play.mvc.Result;
import views.components.datatable.DatatableResponse;

public class Manips extends CommonController {

	//final static Form<Manip> runForm = form(Manip.class);
	final static DynamicForm listForm = new DynamicForm();

	public static Result list(){
		DynamicForm filledForm =  listForm.bindFromRequest();
		LimsManipDAO  limsManipDAO = Spring.getBeanOfType(LimsManipDAO.class);
		Logger.info("Project Value :"+getProjetValue());
		List<Manip> manips = limsManipDAO.getManips(getEtmanipValue(),getEtmaterielmanipValue(),getProjetValue());
		Logger.info("Manips nb "+manips.size());
		Logger.info("Etmanip "+getEtmanipValue());
		return ok(Json.toJson(new DatatableResponse(manips, manips.size())));
	}

	private static String getProjetValue() {

		try{
			return request().queryString().get("project")[0];
		}catch(Exception e){
			Logger.error(e.getMessage());
			return null; // default value;
		}
	}

	private static Integer getEtmanipValue() {
		try{
			return Integer.valueOf(request().queryString().get("etmanip")[0]);
		}catch(Exception e){
			Logger.error(e.getMessage());
			return null; // default value;
		}
	}
	
	
	private static Integer getEtmaterielmanipValue(){
		try{
			return Integer.valueOf(request().queryString().get("emateriel")[0]);
		}catch(Exception e){
			Logger.error(e.getMessage());
			return 2; // default value;
		}
		
	}
}
