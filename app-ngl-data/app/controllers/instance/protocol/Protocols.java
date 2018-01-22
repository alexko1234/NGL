package controllers.instance.protocol;

import models.Constants;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.mvc.Result;
import services.instance.protocol.ProtocolServiceCNG;
import services.instance.protocol.ProtocolServiceCNS;
import validation.ContextValidation;

import javax.inject.Inject;

import controllers.CommonController;
import controllers.NGLBaseController;
import fr.cea.ig.play.NGLContext;

public class Protocols extends NGLBaseController { //CommonController {
	
	public static ALogger logger= Logger.of("Protocols");
		
	@Inject
	public Protocols(NGLContext ctx) {
		super(ctx);
	}
	
	public /*static*/ Result save(){
		ContextValidation ctx = new ContextValidation(Constants.NGL_DATA_USER);
		ctx.setCreationMode();
		try {
			if (play.Play.application().configuration().getString("institute").equals("CNS")){
				ProtocolServiceCNS.main(ctx);
			}else if(play.Play.application().configuration().getString("institute").equals("CNG")){
				ProtocolServiceCNG.main(ctx);
			}else if(play.Play.application().configuration().getString("institute").equals("TEST")){
				ProtocolServiceCNS.main(ctx);
			}else{
				Logger.error("You need to specify only one institute ! Now, it's "+ play.Play.application().configuration().getString("institute"));
			}
			
			if (ctx.errors.size() > 0) {
				ctx.displayErrors(logger);
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

