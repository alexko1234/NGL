package controllers.manips.api;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.List;

import javax.inject.Inject;

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
import fr.cea.ig.play.NGLContext;

public class Manips extends CommonController {

	private final NGLContext ctx;
	@Inject
	public Manips(NGLContext ctx) {
		this.ctx = ctx;
		manipForm = ctx.form(MaterielManipSearch.class);
	}
	final /*static*/ Form<MaterielManipSearch> manipForm;// = form(MaterielManipSearch.class);
	
	public /*static*/ Result list(){
		Form<MaterielManipSearch> filledForm =  manipForm.bindFromRequest();
		LimsManipDAO  limsManipDAO = Spring.getBeanOfType(LimsManipDAO.class);
		Logger.debug("Manip Form :"+filledForm.toString());
		List<Manip> manips = limsManipDAO.findManips(filledForm.get().etmanip,filledForm.get().emateriel, filledForm.get().project);
		Logger.debug("Manips nb "+manips.size());
		return ok(Json.toJson(new DatatableResponse(manips, manips.size())));
	}

	
}
