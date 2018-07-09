package controllers;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import fr.cea.ig.play.migration.NGLContext;
import play.data.validation.ValidationError;
import play.libs.Scala;
import play.mvc.Controller;
import play.mvc.Result;
import play.routing.JavaScriptReverseRouter;

/**
 * Controller providing methods that are used across NGL controllers
 * but not tied to application domain objects.
 *   
 * @author vrd
 *
 */
class NGLBaseController extends Controller {
	
	/**
	 * NGL context.
	 */
	protected NGLContext ctx;
	
	@Inject
	/**
	 * DI constructor.
	 * @param ctx NGL context
	 */
	public NGLBaseController(NGLContext ctx) {
		this.ctx = ctx;
	}
	
	/**
	 * Get current user from HTTP context.
	 * @return current user
	 */
	protected String getCurrentUser(){
		return ctx.currentUser();
	}
	
	/**
	 * Formats errors as a proper JSON result.
	 * @param errors errors to format
	 * @return       JSON formatted errors
	 */
	public JsonNode errorsAsJson(Map<String, List<ValidationError>> errors) {
		return ctx.errorsAsJson(errors);
	}
	
	/**
	 * Javascript routes.
	 * @param routes routes to provide as javascript
	 * @return       routes javascript
	 */
	public Result jsRoutes(play.api.routing.JavaScriptReverseRoute... routes) {
		return ok(JavaScriptReverseRouter.create("jsRoutes",routes)).as("text/javascript");
	}

	public Result jsAppURL() {
		StringBuilder sb = 
				new StringBuilder()
					.append("function AppURL (app){")
					.append("if(app===\"sq\") return \"")
					.append(ctx.config().getSQUrl())
					.append("\"; else if(app===\"bi\") return \"")
					.append(ctx.config().getBIUrl())
					.append("\"; else if(app===\"project\") return \"")
					.append(ctx.config().getProjectUrl())
					.append("\";}");
		return ok(sb.toString()).as("application/javascript");
	}

	public Result jsMessages() {
		return ok(ctx.jsMessages().apply(Scala.Option("Messages"), 
				                         jsmessages.japi.Helper.messagesFromCurrentHttpContext())).as("application/javascript");
	}

	public Result jsPrintTag(){
		boolean tag = ctx.config().isBarCodePrintingEnabled();		
		String js = "PrintTag={}; PrintTag.isActive =(function(){return " + tag + ";});";
		return ok(js).as("application/javascript");
	}
	
}
