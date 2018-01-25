package controllers.sra.submissions.tpl;

// import play.Routes;
import play.routing.JavaScriptReverseRouter;
import play.mvc.Result;

import views.html.submissions.activate;
import views.html.submissions.consultation;
import views.html.submissions.create;
import views.html.submissions.details;
import views.html.submissions.home;
import views.html.submissions.validation;

import javax.inject.Inject;

import controllers.NGLBaseController;
import fr.cea.ig.authentication.Authenticated;
import fr.cea.ig.authorization.Authorized;
import fr.cea.ig.lfw.Historized;
import fr.cea.ig.play.NGLContext;

// import controllers.CommonController;


//public class Submissions extends -CommonController {
public class Submissions extends NGLBaseController {
	
	private final home         home;
	private final create       create;
	private final details      details;
	private final activate     activate;
	private final consultation consultation;
	private final validation   validation;
	
	@Inject
	public Submissions(NGLContext ctx, home home, create create, details details, activate activate, consultation consultation, validation validation) {
		super(ctx);
		this.home         = home;
		this.create       = create;
		this.details      = details;
		this.activate     = activate;
		this.consultation = consultation;	
		this.validation   = validation;	
	}
	
	@Authenticated
	@Historized
	@Authorized.Read 
	public Result home(String homecode) {
		return ok(home.render(homecode));
	}
	
	@Authenticated
	@Historized
	@Authorized.Read 
	public Result create() {
		return ok(create.render());
	}
	
	@Authenticated
	@Historized
	@Authorized.Read 
	public Result get(String code) {
		return ok(home.render("search"));
	}
	
	@Authenticated
	@Historized
	@Authorized.Read 
	public Result details() {
		return ok(details.render());
	}
	
	@Authenticated
	@Historized
	@Authorized.Read 
	public Result activate()	{
		return ok(activate.render());
	}	
	
	@Authenticated
	@Historized
	@Authorized.Read 
	public Result consultation()	{
		return ok(consultation.render());
	}

	@Authenticated
	@Historized
	@Authorized.Read 
	public Result validation()	{
		return ok(validation.render());
	}
	
	public Result javascriptRoutes() {
		return jsRoutes(controllers.sra.submissions.tpl.routes.javascript.Submissions.home(),
						controllers.projects.api.routes.javascript.Projects.list(),
						controllers.sra.api.routes.javascript.Variables.get(),
						controllers.sra.api.routes.javascript.Variables.list(),
						controllers.commons.api.routes.javascript.States.list(),
						controllers.sra.studies.api.routes.javascript.Studies.list(),
						controllers.sra.studies.api.routes.javascript.Studies.get(),
						controllers.sra.studies.api.routes.javascript.Studies.update(),
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
						controllers.sra.samples.api.routes.javascript.Samples.get(),
						controllers.sra.samples.api.routes.javascript.Samples.update(),
						controllers.sra.experiments.api.routes.javascript.Experiments.list(),
						controllers.sra.experiments.api.routes.javascript.Experiments.get(),
						controllers.sra.experiments.api.routes.javascript.Experiments.update());
	}

	/*
	public Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(  	    		
				//Routes.javascriptRouter("jsRoutes",
				JavaScriptReverseRouter.create("jsRoutes",
						// Routes
						controllers.sra.submissions.tpl.routes.javascript.Submissions.home(),
						controllers.projects.api.routes.javascript.Projects.list(),
						controllers.sra.api.routes.javascript.Variables.get(),
						controllers.sra.api.routes.javascript.Variables.list(),
						controllers.commons.api.routes.javascript.States.list(),
						controllers.sra.studies.api.routes.javascript.Studies.list(),
						controllers.sra.studies.api.routes.javascript.Studies.get(),
						controllers.sra.studies.api.routes.javascript.Studies.update(),
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
						controllers.sra.samples.api.routes.javascript.Samples.get(),
						controllers.sra.samples.api.routes.javascript.Samples.update(),
						controllers.sra.experiments.api.routes.javascript.Experiments.list(),
						controllers.sra.experiments.api.routes.javascript.Experiments.get(),
						controllers.sra.experiments.api.routes.javascript.Experiments.update()    		
						)	  	      
				);
	}
	*/
}
