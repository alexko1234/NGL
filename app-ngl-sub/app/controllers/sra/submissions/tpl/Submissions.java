package controllers.sra.submissions.tpl;

import play.Routes;
import play.mvc.Result;
import views.html.submissions.activate;
import views.html.submissions.consultation;
import views.html.submissions.create;
import views.html.submissions.details;
import views.html.submissions.home;
import controllers.CommonController;


public class Submissions extends CommonController
{
	
	public static Result home(String homecode) {
		return ok(home.render(homecode));
	}
	

	public static Result create() {
		return ok(create.render());
	}
	
	
	public static Result get(String code)
	{
		return ok(home.render("search"));
	}
	
	public static Result details()
	{
		return ok(details.render());

	}
	public static Result activate()
	{
		return ok(activate.render());

	}	
	public static Result consultation()
	{
		return ok(consultation.render());

	}


	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	    		  // Routes
  	    		controllers.sra.submissions.tpl.routes.javascript.Submissions.home(),
  	    		controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.sra.api.routes.javascript.Variables.get(),
  	    		controllers.commons.api.routes.javascript.States.list(),
 	    		controllers.sra.studies.api.routes.javascript.Studies.list(),
 	    		controllers.sra.studies.api.routes.javascript.Studies.get(),
  	    		controllers.sra.configurations.api.routes.javascript.Configurations.list(),
  	    		controllers.sra.configurations.api.routes.javascript.Configurations.get(),
  	    		controllers.readsets.api.routes.javascript.ReadSets.list(),
  	    		controllers.sra.submissions.api.routes.javascript.Submissions.list(),
  	    		controllers.sra.submissions.api.routes.javascript.Submissions.save(),
  	    		controllers.sra.submissions.api.routes.javascript.Submissions.get(),
  	    		controllers.sra.submissions.api.routes.javascript.Submissions.update(),
  	    		controllers.sra.submissions.api.routes.javascript.Submissions.updateState(),
  	    		controllers.sra.submissions.api.routes.javascript.Submissions.activate(),
 	    		controllers.sra.submissions.tpl.routes.javascript.Submissions.get(),
  	    		controllers.sra.samples.api.routes.javascript.Samples.list(),
  	    		controllers.sra.samples.api.routes.javascript.Samples.update(),
  	  	    	controllers.sra.experiments.api.routes.javascript.Experiments.list(),
  	    		controllers.sra.experiments.api.routes.javascript.Experiments.update()    		
    	   )	  	      
  	    );
  	  }   		
}
