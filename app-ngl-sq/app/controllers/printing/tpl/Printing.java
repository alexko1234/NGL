package controllers.printing.tpl;

//import play.Routes;
import play.routing.JavaScriptReverseRouter;

import javax.inject.Inject;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.printing.home;


// TODO: clean, comment

public class Printing extends Controller {

	private final home home;

	@Inject
	public Printing(home home) {
		this.home = home;
	}

	public /*static*/ Result home(String code){
		return ok(home.render(code));
	}

	public /*static*/ Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(  	    		
				// Routes.javascriptRouter("jsRoutes",
				JavaScriptReverseRouter.create("jsRoutes",
						// Routes
						controllers.printing.api.routes.javascript.Tags.list(),
						controllers.printing.api.routes.javascript.Tags.print(),
						controllers.commons.api.routes.javascript.Parameters.list(),
						controllers.printing.tpl.routes.javascript.Printing.home(),
						controllers.printing.tpl.routes.javascript.Tags.display()

						)	  	      
				);
	}
}
