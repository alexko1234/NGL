package controllers.submissions.tpl;

import controllers.CommonController;
import play.Routes;
import play.mvc.Result;
import views.html.submissions.home;
import views.html.submissions.create;
import views.html.submissions.details;
import controllers.readsets.api.ReadSetsController;

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
		// todo remettre details.render et import views.html.submissions.details;
		return ok(details.render());

	}
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	    		  // Routes
  	    		controllers.submissions.tpl.routes.javascript.Submissions.home(),
  	    		controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.sra.api.routes.javascript.Variables.get(),
  	    		controllers.studies.api.routes.javascript.Studies.list(),
  	    		controllers.configurations.api.routes.javascript.Configurations.list(),
  	    		controllers.readsets.api.routes.javascript.ReadSets.list(),
  	    		controllers.submissions.api.routes.javascript.Submissions.list(),
  	    		controllers.submissions.api.routes.javascript.Submissions.save(),
  	    		controllers.submissions.api.routes.javascript.Submissions.get(),
  	    		controllers.submissions.tpl.routes.javascript.Submissions.get(),
  	    		controllers.samples.api.routes.javascript.Samples.list(),
  	    		controllers.samples.api.routes.javascript.Samples.update()
  	      )	  	      
  	    );
  	  }
  	 
}
