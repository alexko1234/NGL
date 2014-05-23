package controllers.stats.tpl;

import play.Routes;
import play.mvc.Result;
import views.html.stats.config;
import views.html.stats.home;
import views.html.stats.show;
import controllers.CommonController;

/**
 * Controller around Run object
 * @author galbini
 *
 */
public class Stats extends CommonController {
	
	public static Result home(String homecode) {
		return ok(home.render(homecode));
	}
	
	public static Result config(String type) {
		return ok(config.render());
	}
	
	public static Result show() {
		return ok(show.render());
	}
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("statsJsRoutes",
  	        // Routes
  	    		controllers.stats.tpl.routes.javascript.Stats.home(),  
  	    		controllers.stats.tpl.routes.javascript.Stats.config()
  	      )	  	      
  	    );
  	  }
	
}
