package controllers;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {


  public static Result index(String id) {
    return ok(index.render("Welcome to the Next LIMS Generation", id));
  }

  public static Result javascriptRoutes() {
	  	    response().setContentType("text/javascript");
	  	    return ok(
	  	    		
	  	      Routes.javascriptRouter("jsRoutes",
	  	        // Routes
	  	    		controllers.run.routes.javascript.Runs.show(),
	  	    		controllers.run.routes.javascript.Runs.createOrUpdate()
	  	      )	  	      
	  	    );
	  	  }
}