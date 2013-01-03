package controllers;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import views.html.index;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;


public class Application extends Controller {

  public static Result index(String id) {
    return ok(index.render("Welcome to the Next LIMS Generation", id));
  }

  public static Result javascriptRoutes() {
	  	    response().setContentType("text/javascript");
	  	    return ok(
	  	      
	  	        Routes.javascriptRouter("jsRoutes",	  	       
	  	        // Routes	  	       
	  	        controllers.admin.types.routes.javascript.GenericTypes.show(),
	  	        controllers.admin.types.routes.javascript.GenericTypes.createOrUpdate(),
	  	      controllers.admin.types.routes.javascript.GenericTypes.add(),
	  	        controllers.administration.authentication.routes.javascript.User.logOut()
	  	        
	  	      )
	  	    );
	  	  }
}