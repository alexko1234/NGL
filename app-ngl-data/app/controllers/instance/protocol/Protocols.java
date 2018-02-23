package controllers.instance.protocol;

import static fr.cea.ig.play.IGGlobals.configuration;

import javax.inject.Inject;

import com.typesafe.config.Config;

import fr.cea.ig.ngl.NGLConfig;
import models.Constants;
//import play.Logger;
//import play.Logger.ALogger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.instance.protocol.ProtocolServiceCNG;
import services.instance.protocol.ProtocolServiceCNS;
import validation.ContextValidation;

//import javax.inject.Inject;

//import controllers.CommonController;
//import controllers.NGLBaseController;
//import fr.cea.ig.play.NGLContext;

public class Protocols extends Controller { // NGLBaseController { //CommonController {
	
	public static play.Logger.ALogger logger = play.Logger.of("Protocols");
		
//	@Inject
//	public Protocols(NGLContext ctx) {
//		super(ctx);
//	}
	
	private final String institute;
	
	@Inject
	public Protocols(NGLConfig config) {
		institute = config.getInstitute();
	}
	
	public Result save(){
		ContextValidation ctx = new ContextValidation(Constants.NGL_DATA_USER);
		ctx.setCreationMode();
		try {
//			if (play.Play.application().configuration().getString("institute").equals("CNS")) {
//				ProtocolServiceCNS.main(ctx);
//			} else if(play.Play.application().configuration().getString("institute").equals("CNG")) {
//				ProtocolServiceCNG.main(ctx);
//			} else if(play.Play.application().configuration().getString("institute").equals("TEST")) {
//				ProtocolServiceCNS.main(ctx);
//			} else {
//				logger.error("You need to specify only one institute ! Now, it's "+ play.Play.application().configuration().getString("institute"));
//			}
			switch (institute) {
			case "CNS"  : ProtocolServiceCNS.main(ctx); break;
			case "CNG"  : ProtocolServiceCNG.main(ctx); break;
			case "TEST" : ProtocolServiceCNS.main(ctx); break;
			default     : logger.error("You need to specify only one institute ! Now, it's {}", institute);
			}
			if (ctx.errors.size() > 0) {
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

