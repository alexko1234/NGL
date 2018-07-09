package controllers.instance.parameters;

import static fr.cea.ig.play.IGGlobals.configuration;

import models.Constants;
//import play.Logger;
//import play.Logger.ALogger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.instance.parameter.PrinterCNS;
//import services.instance.protocol.ProtocolServiceCNG;
//import services.instance.protocol.ProtocolServiceCNS;
import validation.ContextValidation;

//import javax.inject.Inject;

//import controllers.CommonController;
//import controllers.NGLBaseController;
// import fr.cea.ig.play.migration.NGLContext;

public class Printers extends Controller { // extends NGLBaseController { //CommonController {
	
	public static final play.Logger.ALogger logger = play.Logger.of(Printers.class);
		
//	@Inject
//	public Printers(NGLContext ctx) {
//		super(ctx);
//	}
	
	public Result save(){
		ContextValidation ctx = new ContextValidation(Constants.NGL_DATA_USER);
		ctx.setCreationMode();
		try {
//<<<<<<< HEAD
////			if (play.Play.application().configuration().getString("institute").equals("CNS")){
////				PrinterCNS.main(ctx);
////			}else if(play.Play.application().configuration().getString("institute").equals("CNG")){
////			}else
			if(play.Play.application().configuration().getString("institute").equals("GET")){
				
			}else{
				Logger.error("You need to specify only one institute ! Now, it's "+ play.Play.application().configuration().getString("institute"));
//=======
//			String institute = configuration().getString("institute");
////			if (play.Play.application().configuration().getString("institute").equals("CNS")) {
////				PrinterCNS.main(ctx);
////			} else if(play.Play.application().configuration().getString("institute").equals("CNG")) {
////				
////			} else if(play.Play.application().configuration().getString("institute").equals("TEST")) {
////				
////			} else {
////				Logger.error("You need to specify only one institute ! Now, it's "+ play.Play.application().configuration().getString("institute"));
////			}
//			switch (institute) {
//			case "CNS"  : PrinterCNS.main(ctx); break; 
//			case "CNG"  : break;
//			case "TEST" : break;
//			default     : logger.error("You need to specify only one institute ! Now, it's " + institute);
//>>>>>>> V2.0.2
			}
			if (ctx.hasErrors()) {
				ctx.displayErrors(logger);
				return badRequest(Json.toJson(ctx.errors));
			} else {
				return ok();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return internalServerError(e.getMessage());
		}	
	}
	
}

