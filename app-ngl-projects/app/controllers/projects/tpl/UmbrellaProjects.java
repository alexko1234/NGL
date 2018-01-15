package controllers.projects.tpl;

import javax.inject.Inject;

import controllers.NGLBaseController;
//import controllers.CommonController;
import controllers.projects.tpl.routes.javascript;
import fr.cea.ig.play.NGLContext;
//import play.Routes;
import play.routing.JavaScriptReverseRouter;
import play.mvc.Result;
import views.html.umbrellaprojects.*;

/**
 * Controller around UmbrellaProject object
 * 
 * @author dnoisett
 *
 */
// public class UmbrellaProjects extends CommonController {
public class UmbrellaProjects extends NGLBaseController {
	
	private home home;
	
	@Inject
	public UmbrellaProjects(NGLContext ctx, home home) {
		super(ctx);
		this.home = home;
	}
	
	public Result home(String homecode) {
		return ok(home.render(homecode));
	}
	
	public Result get(String code) {
		return ok(home.render("search")); 
	}
	
	public Result search(String type) {
		if (!"add".equals(type)) {
			return ok(search.render(Boolean.TRUE));
		} else {
			return ok(search.render(Boolean.FALSE));
		}
	}

	public Result details(String typeForm) {
		return ok(details.render(typeForm));
	}
	
	public Result javascriptRoutes() {
  	    return jsRoutes(controllers.projects.tpl.routes.javascript.UmbrellaProjects.home(),  
  	    				controllers.projects.tpl.routes.javascript.UmbrellaProjects.get(), 
  	    				controllers.projects.tpl.routes.javascript.UmbrellaProjects.search(),
  	    				controllers.projects.tpl.routes.javascript.UmbrellaProjects.details(),
  	    				controllers.projects.api.routes.javascript.UmbrellaProjects.get(),
  	    				controllers.projects.api.routes.javascript.UmbrellaProjects.update(),
  	    				controllers.projects.api.routes.javascript.UmbrellaProjects.list(),
  	    				controllers.projects.api.routes.javascript.UmbrellaProjects.save()
  	    				//,controllers.umbrellaprojects.api.routes.javascript.UmbrellaProjects.delete()
  	    		);
  	  }
	
/*	
	public Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      //Routes.javascriptRouter("jsRoutes",
  	    		JavaScriptReverseRouter.create("jsRoutes",
  	        // Routes
  	    		controllers.projects.tpl.routes.javascript.UmbrellaProjects.home(),  
  	    		controllers.projects.tpl.routes.javascript.UmbrellaProjects.get(), 
  	    		controllers.projects.tpl.routes.javascript.UmbrellaProjects.search(),
  	    		controllers.projects.tpl.routes.javascript.UmbrellaProjects.details(),
  	    		controllers.projects.api.routes.javascript.UmbrellaProjects.get(),
  	    		controllers.projects.api.routes.javascript.UmbrellaProjects.update(),
  	    		controllers.projects.api.routes.javascript.UmbrellaProjects.list(),
  	    		controllers.projects.api.routes.javascript.UmbrellaProjects.save()
  	    		//,controllers.umbrellaprojects.api.routes.javascript.UmbrellaProjects.delete()
  	    	)	  	      
  	    );
  	  }
	*/
	
}

