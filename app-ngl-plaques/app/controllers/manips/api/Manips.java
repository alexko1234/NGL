package controllers.manips.api;

import static play.data.Form.form;

import java.util.List;

import lims.cns.dao.LimsManipDAO;
import lims.models.Manip;
import play.Logger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;
import controllers.MaterielManipSearch;

public class Manips extends CommonController {

	final static Form<MaterielManipSearch> manipForm = form(MaterielManipSearch.class);
	
	public static Result list(){
		Form<MaterielManipSearch> filledForm =  manipForm.bindFromRequest();
		LimsManipDAO  limsManipDAO = Spring.getBeanOfType(LimsManipDAO.class);
		Logger.debug("Manip Form :"+filledForm.toString());
		List<Manip> manips = limsManipDAO.findManips(filledForm.get().etmanip,filledForm.get().emateriel, filledForm.get().project);
		Logger.debug("Manips nb "+manips.size());
		return ok(Json.toJson(new DatatableResponse(manips, manips.size())));
	}

	
}
