package controllers.projects.tpl;

import java.util.ArrayList;
import java.util.List;

import controllers.CommonController;
import play.Routes;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.projects.*;

/**
 * Controller around Run object
 * @author galbini
 *
 */
public class Projects extends CommonController {
	
	public static Result get(String code) {
		return ok(home.render()); 
	}
	
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.projects.tpl.routes.javascript.Projects.get()
  	      )	  	      
  	    );
  	  }
	
}
