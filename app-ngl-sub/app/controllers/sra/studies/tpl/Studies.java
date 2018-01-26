package controllers.sra.studies.tpl;

import javax.inject.Inject;

import controllers.CommonController;
//import play.Routes;
import play.routing.JavaScriptReverseRouter;
import play.mvc.Result;
import views.html.studies.home;
import views.html.studies.create;
import views.html.studies.consultation;
import views.html.studies.details;


public class Studies extends CommonController {
	private final home home;
	private final create create;
	private final consultation consultation;
	private final details details;
	@Inject
	public Studies(home home, create create, consultation consultation, details details) {
		this.home         = home; 
		this.create       = create;
		this.consultation = consultation;
		this.details      = details;
	}
	public Result home(String homecode) {
		return ok(home.render(homecode));
	}

	public Result create() {
		return ok(create.render());
	}
	
	public Result consultation() {
		return ok(consultation.render());
	}	
	
	/*public static Result release() {
		return ok(release.render());
	}	
	*/
	public Result get(String code) {
		return ok(home.render("search"));
	}

	public Result details() {
		return ok(details.render());
	}	
	


	public Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	    		// Routes.javascriptRouter("jsRoutes",
  	    		// Routes
  	    		JavaScriptReverseRouter.create("jsRoutes",
  	    				controllers.sra.studies.tpl.routes.javascript.Studies.home(),
  	    				controllers.projects.api.routes.javascript.Projects.list(),
  	    				controllers.commons.api.routes.javascript.States.list(),
  	    				controllers.sra.api.routes.javascript.Variables.get(),
  	    				controllers.sra.api.routes.javascript.Variables.list(),
  	    				controllers.sra.studies.api.routes.javascript.Studies.save(),
  	    				controllers.sra.studies.api.routes.javascript.Studies.get(),
  	    				controllers.sra.studies.tpl.routes.javascript.Studies.get(),
  	    				controllers.sra.studies.api.routes.javascript.Studies.list(),
  	    				controllers.sra.studies.api.routes.javascript.Studies.update(),
  	    				controllers.sra.studies.api.routes.javascript.Studies.release(),
  	    				controllers.sra.studies.api.routes.javascript.Studies.updateState(),
  	    				controllers.sra.samples.api.routes.javascript.Samples.list(),
  	    				controllers.sra.samples.api.routes.javascript.Samples.get(),
  	    				controllers.sra.experiments.api.routes.javascript.Experiments.list(),
  	    				controllers.sra.experiments.api.routes.javascript.Experiments.get()
  	    				)
  	    		);
	}
}
