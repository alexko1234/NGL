package controllers.projects.tpl;

import controllers.CommonController;
import play.Routes;
import play.mvc.Result;
import views.html.projects.*;

/**
 * Controller around Project object
 * @author dnoisett
 *
 */
public class Projects extends CommonController {
	
	public static Result home(String homecode) {
		return ok(home.render(homecode));
	}
	
	public static Result get(String code) {
		return ok(home.render("search")); 
	}
	
	public static Result search(String type) {
		return ok(search.render(Boolean.TRUE));
	}

	
	public static Result details() {
		return ok(details.render());
	}
	
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.projects.tpl.routes.javascript.Projects.home(),  
  	    		controllers.projects.tpl.routes.javascript.Projects.get(), 
  	    		controllers.projects.tpl.routes.javascript.Projects.search(),
  	    		controllers.projects.api.routes.javascript.Projects.get(),
  	    		controllers.projects.api.routes.javascript.Projects.update(),
  	    		controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.projects.api.routes.javascript.Projects.save(),
  	    		controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
  	    		controllers.commons.api.routes.javascript.States.list(),
  	    		controllers.projects.api.routes.javascript.ProjectTypes.list(),
  	    		controllers.projects.api.routes.javascript.ProjectCategories.list(),
  	    		controllers.umbrellaprojects.api.routes.javascript.UmbrellaProjects.list()
  	    	)	  	      
  	    );
  	  }
	
}

