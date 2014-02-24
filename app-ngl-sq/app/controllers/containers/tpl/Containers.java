package controllers.containers.tpl;

import java.util.ArrayList;
import java.util.List;

import play.Routes;
import play.i18n.Messages;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;
import views.html.container.home;
import views.html.container.search;
import controllers.CommonController;

public class Containers extends CommonController {

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
  	    		controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.samples.api.routes.javascript.Samples.list(),
  	    		controllers.containers.api.routes.javascript.Containers.list(),
  	    		controllers.experiments.api.routes.javascript.ExperimentTypes.list(),
  	    		controllers.containers.api.routes.javascript.ContainerSupportCategories.list(),
  	    		controllers.containers.api.routes.javascript.Containers.list_supports(),
  	    		controllers.containers.api.routes.javascript.ContainerCategories.list(),
  	    		controllers.commons.api.routes.javascript.States.list(),
  	    		controllers.containers.api.routes.javascript.ContainerCategories.list(),
  	    		controllers.containers.tpl.routes.javascript.Containers.search(),
  	    		controllers.containers.tpl.routes.javascript.Containers.home()
  	      )	  	      
  	    );
  	}
}
