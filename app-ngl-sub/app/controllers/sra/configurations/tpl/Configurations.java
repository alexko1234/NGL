package controllers.sra.configurations.tpl;

import controllers.CommonController;
import play.Routes;
import play.mvc.Result;
import views.html.configurations.home;
import views.html.configurations.create;
import views.html.configurations.consultation;
//import views.html.configurations.details;

public class Configurations extends CommonController
{
	
	public static Result home(String homecode) {
		return ok(home.render(homecode));
	}

	public static Result create() {
		return ok(create.render());
	}
	
	public static Result consultation() {
		return ok(consultation.render());
	}	

	public static Result get(String code) {
		return ok(home.render("search"));
	}
	
/*	public static Result details() {
		return ok(details.render());
	}	
*/	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.sra.configurations.tpl.routes.javascript.Configurations.home(),
  	    		controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.sra.configurations.api.routes.javascript.Configurations.save(),
  	    		controllers.sra.api.routes.javascript.Variables.get(),
  	    		controllers.sra.configurations.api.routes.javascript.Configurations.get(),
 	    		controllers.sra.configurations.tpl.routes.javascript.Configurations.get(),
  	    		controllers.sra.configurations.api.routes.javascript.Configurations.list(),
  	    		controllers.sra.configurations.api.routes.javascript.Configurations.update()	
  	     	)
  	    );
  	  }
	

  	 
}
