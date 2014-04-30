package controllers.umbrellaprojects.tpl;

import java.util.ArrayList;
import java.util.List;

import controllers.CommonController;
import play.Routes;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;
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
	
	public static Result add() {
		return ok(add.render()); 
	}

	
	public static Result details() {
		return ok(details.render());
	}
	
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.umbrellaprojects.tpl.routes.javascript.UmbrellaProjects.home(),  
  	    		controllers.umbrellaprojects.tpl.routes.javascript.UmbrellaProjects.get(), 
  	    		controllers.umbrellaprojects.tpl.routes.javascript.UmbrellaProjects.add(),
  	    		controllers.umbrellaprojects.tpl.routes.javascript.UmbrellaProjects.search(),
  	    		controllers.umbrellaprojects.tpl.routes.javascript.UmbrellaProjects.details(),
  	    		controllers.umbrellaprojects.api.routes.javascript.UmbrellaProjects.get(),
  	    		controllers.umbrellaprojects.api.routes.javascript.UmbrellaProjects.update(),
  	    		controllers.umbrellaprojects.api.routes.javascript.UmbrellaProjects.list(),
  	    		controllers.umbrellaprojects.api.routes.javascript.UmbrellaProjects.save(),
  	    		controllers.projects.api.routes.javascript.Projects.list()
  	    		//,controllers.umbrellaprojects.api.routes.javascript.UmbrellaProjects.delete()
  	    	)	  	      
  	    );
  	  }
	
}

