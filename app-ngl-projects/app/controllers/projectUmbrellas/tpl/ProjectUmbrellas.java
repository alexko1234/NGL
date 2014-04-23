package controllers.projectUmbrellas.tpl;

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
import views.html.projectUmbrellas.*;

/**
 * Controller around Projectumbrella object
 * @author dnoisett
 *
 */
public class ProjectUmbrellas extends CommonController {
	
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
  	    		controllers.projectUmbrellas.tpl.routes.javascript.ProjectUmbrellas.home(),  
  	    		controllers.projectUmbrellas.tpl.routes.javascript.ProjectUmbrellas.get(), 
  	    		controllers.projectUmbrellas.tpl.routes.javascript.ProjectUmbrellas.add(),
  	    		controllers.projectUmbrellas.tpl.routes.javascript.ProjectUmbrellas.search(),
  	    		controllers.projectUmbrellas.api.routes.javascript.ProjectUmbrellas.get(),
  	    		controllers.projectUmbrellas.api.routes.javascript.ProjectUmbrellas.update(),
  	    		controllers.projectUmbrellas.api.routes.javascript.ProjectUmbrellas.list(),
  	    		controllers.projectUmbrellas.api.routes.javascript.ProjectUmbrellas.save(),
  	    		controllers.projects.api.routes.javascript.Projects.list()
  	    		//,controllers.projectUmbrellas.api.routes.javascript.ProjectUmbrellas.delete()
  	    	)	  	      
  	    );
  	  }
	
}

