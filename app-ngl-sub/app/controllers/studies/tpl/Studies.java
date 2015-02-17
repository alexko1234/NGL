package controllers.studies.tpl;

import controllers.CommonController;
import play.Routes;
import play.mvc.Result;
import views.html.studies.home;
import views.html.studies.create;

public class Studies extends CommonController
{
	
	public static Result home(String homecode) {
		return ok(home.render(homecode));
	}

	public static Result create() {
		return ok(create.render());
	}
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.studies.tpl.routes.javascript.Studies.home(),
  	    		controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.studies.api.routes.javascript.Studies.listTypes(),
  	    		controllers.studies.api.routes.javascript.Studies.save()
  	      )	  	      
  	    );
  	  }
}
