package controllers.printing.tpl;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.printing.home;



public class Printing extends Controller {
	public static Result home(String code){
		return ok(home.render(code));
	}
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.printing.api.routes.javascript.Tags.list(),
  	    		controllers.printing.tpl.routes.javascript.Tags.display()
  	    		
  	      )	  	      
  	    );
  	}
}
