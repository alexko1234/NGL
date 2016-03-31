package controllers.admin.supports.tpl;

import play.Routes;
import play.mvc.Result;
import views.html.admin.supports.*;
import controllers.CommonController;

public class Supports extends CommonController{

	
	public static Result home(String code){
		return ok(home.render(code));
	}
	
	public static Result search(String code){
		if("switch-index".equals(code))
			return ok(searchSwitchIndex.render());
		else{
			return ok();
		}
	}

	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.samples.api.routes.javascript.Samples.list(),
  	    		controllers.containers.api.routes.javascript.Containers.list(),
  	    		controllers.containers.api.routes.javascript.ContainerSupports.list(),
  	    		controllers.experiments.api.routes.javascript.Experiments.list(),
  	    		controllers.commons.api.routes.javascript.States.list(),
  	    		controllers.commons.api.routes.javascript.Users.list(),
	      		controllers.commons.api.routes.javascript.Values.list(),
	      		controllers.commons.api.routes.javascript.Parameters.list(),
	      		controllers.admin.supports.tpl.routes.javascript.Supports.home(),
	      		controllers.admin.supports.tpl.routes.javascript.Supports.search(),
	      		controllers.admin.supports.api.routes.javascript.NGLObjects.list(),
	      		controllers.admin.supports.api.routes.javascript.NGLObjects.update()
  	      )	  	      
  	    );
  	}
}
