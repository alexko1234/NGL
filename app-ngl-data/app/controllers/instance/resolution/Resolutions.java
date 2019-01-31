package controllers.instance.resolution;

import models.Constants;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import services.instance.resolution.ResolutionServiceGET;
import validation.ContextValidation;

//import javax.inject.Inject;

//import controllers.CommonController;
//import controllers.NGLBaseController;
//import fr.cea.ig.play.migration.NGLContext;

public class Resolutions extends Controller { // NGLBaseController { //CommonController {
	
//	@Inject
//	public Resolutions(NGLContext ctx) {
//		super(ctx);
//	}
	
	public Result save(){
		ContextValidation ctx = new ContextValidation(Constants.NGL_DATA_USER);
		ctx.setCreationMode();
		try {
			ResolutionServiceGET.main(ctx);
			if (ctx.errors.size() > 0) {
				return badRequest(Json.toJson(ctx.errors));
			} else {
				return ok();
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return internalServerError(e.getMessage());
		}	
	}
	
}


