package controllers.umbrellaprojects.tpl;

import controllers.CommonController;
import play.Routes;
import play.mvc.Result;
import views.html.umbrellaprojects.*;

/**
 * Controller around UmbrellaProject object
 * @author dnoisett
 *
 */
public class UmbrellaProjects extends CommonController {
	
	public static Result home(String homecode) {
		return ok(home.render(homecode));
	}
	
	public static Result get(String code) {
		return ok(home.render("search")); 
	}
	
	public static Result search(String type) {
		if(!"add".equals(type)){
			return ok(search.render(Boolean.TRUE));
		}else{
			return ok(search.render(Boolean.FALSE));
		}
	}

	
	public static Result details(String typeForm) {
		return ok(details.render(typeForm));
	}
	
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.umbrellaprojects.tpl.routes.javascript.UmbrellaProjects.home(),  
  	    		controllers.umbrellaprojects.tpl.routes.javascript.UmbrellaProjects.get(), 
  	    		controllers.umbrellaprojects.tpl.routes.javascript.UmbrellaProjects.search(),
  	    		controllers.umbrellaprojects.tpl.routes.javascript.UmbrellaProjects.details(),
  	    		controllers.umbrellaprojects.api.routes.javascript.UmbrellaProjects.get(),
  	    		controllers.umbrellaprojects.api.routes.javascript.UmbrellaProjects.update(),
  	    		controllers.umbrellaprojects.api.routes.javascript.UmbrellaProjects.list(),
  	    		controllers.umbrellaprojects.api.routes.javascript.UmbrellaProjects.save()
  	    		//,controllers.umbrellaprojects.api.routes.javascript.UmbrellaProjects.delete()
  	    	)	  	      
  	    );
  	  }
	
}

