package controllers.projects.tpl;

import javax.inject.Inject;

import controllers.CommonController;
//import play.Routes;
import play.routing.JavaScriptReverseRouter;

import play.mvc.Result;
import views.html.projects.*;

/**
 * Controller around Project object
 * @author dnoisett
 *
 */
public class Projects extends CommonController {
	
	private home home;
	@Inject
	public Projects(home home) {
		this.home = home;
	}
	
	public Result home(String homecode) {
		return ok(home.render(homecode));
	}
	
	public Result get(String code) {
		return ok(home.render("search")); 
	}
	
	public Result search(String type) {
		return ok(search.render(Boolean.TRUE));
	}

	
	public Result details() {
		return ok(details.render());
	}
	
	
	public Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      //Routes.javascriptRouter("jsRoutes",
  	    		JavaScriptReverseRouter.create("jsRoutes",
  	    				// Routes
  	    		controllers.projects.tpl.routes.javascript.Projects.home(),  
  	    		controllers.projects.tpl.routes.javascript.Projects.get(), 
  	    		controllers.projects.tpl.routes.javascript.Projects.search(),
  	    		controllers.projects.api.routes.javascript.Projects.get(),
  	    		controllers.projects.api.routes.javascript.Projects.update(),
  	    		controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.projects.api.routes.javascript.ProjectBioinformaticParameters.list(),
  	    		controllers.projects.api.routes.javascript.Projects.save(),
  	    		controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
  	    		controllers.commons.api.routes.javascript.States.list(),
  	    		controllers.projects.api.routes.javascript.ProjectTypes.list(),
  	    		controllers.projects.api.routes.javascript.ProjectCategories.list(),
  	    		controllers.projects.api.routes.javascript.UmbrellaProjects.list()
  	    	)	  	      
  	    );
  	  }
	
}

