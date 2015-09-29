package controllers.sra.studies.tpl;

import controllers.CommonController;
import play.Routes;
import play.mvc.Result;
import views.html.studies.home;
import views.html.studies.create;
import views.html.studies.consultation;

public class Studies extends CommonController
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
		
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.sra.studies.tpl.routes.javascript.Studies.home(),
  	    		controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.sra.api.routes.javascript.Variables.get(),
  	    		controllers.sra.studies.api.routes.javascript.Studies.save(),
  	    		controllers.sra.studies.api.routes.javascript.Studies.get(),
 	    		controllers.sra.studies.tpl.routes.javascript.Studies.get(),
  	    		controllers.sra.studies.api.routes.javascript.Studies.list(),
  	    		controllers.sra.studies.api.routes.javascript.Studies.update()
  	     	)  	      	  	      
  	    );
  	  }
}
