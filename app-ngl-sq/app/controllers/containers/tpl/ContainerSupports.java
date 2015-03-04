package controllers.containers.tpl;

import play.Routes;
import play.mvc.Result;
import views.html.containerSupports.home;
import views.html.containerSupports.search;
import controllers.CommonController;
import controllers.containers.tpl.routes.javascript;

public class ContainerSupports extends CommonController{
	
	public static Result home(String code){
		return ok(home.render(code));
	}	
	
	public static Result search(){
		return ok(search.render());
	}
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.containers.tpl.routes.javascript.ContainerSupports.search(),
  	    		controllers.containers.tpl.routes.javascript.ContainerSupports.home(),
  	    		controllers.containers.api.routes.javascript.ContainerSupports.list(),
  	    		controllers.containers.api.routes.javascript.ContainerSupports.updateBatch(),
  	    		controllers.containers.api.routes.javascript.ContainerSupportCategories.list(),
  	    		controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.samples.api.routes.javascript.Samples.list(),
  	    		controllers.containers.api.routes.javascript.Containers.list(),
  	    		controllers.experiments.api.routes.javascript.ExperimentTypes.list(),  	    		
  	    		controllers.commons.api.routes.javascript.States.list(),
  	    		controllers.processes.api.routes.javascript.ProcessTypes.list(),
  	    		controllers.processes.api.routes.javascript.ProcessCategories.list(),
  	    		controllers.containers.api.routes.javascript.ContainerCategories.list(),
  	    		controllers.containers.api.routes.javascript.Containers.updateBatch(),
  	    		controllers.commons.api.routes.javascript.Users.list()
  	    		
  	    		
  	      )	  	      
  	    );
  	}

}
