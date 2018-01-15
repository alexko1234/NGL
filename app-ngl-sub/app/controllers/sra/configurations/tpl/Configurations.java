package controllers.sra.configurations.tpl;

import javax.inject.Inject;

// import controllers.CommonController;
import controllers.NGLBaseController;
import fr.cea.ig.play.NGLContext;
import play.routing.JavaScriptReverseRouter;
import play.mvc.Result;
import views.html.configurations.home;
import views.html.configurations.create;
import views.html.configurations.consultation;
//import views.html.configurations.details;

// public class Configurations extends -CommonController {
public class Configurations extends NGLBaseController {
	
	private home home;
	private create create;
	private consultation consultation;
	
	@Inject
	public Configurations(NGLContext ctx, home home, create create, consultation consultation) {
		super(ctx);
		this.home         = home;
		this.create       = create;
		this.consultation = consultation;
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

	public Result get(String code) {
		return ok(home.render("search"));
	}
	
/*	public static Result details() {
		return ok(details.render());
	}	
*/	

	public Result javascriptRoutes() {
		return jsRoutes(controllers.sra.configurations.tpl.routes.javascript.Configurations.home(),
						controllers.projects.api.routes.javascript.Projects.list(),
						controllers.commons.api.routes.javascript.States.list(),
						controllers.sra.configurations.api.routes.javascript.Configurations.save(),
						controllers.sra.api.routes.javascript.Variables.get(),
						controllers.sra.api.routes.javascript.Variables.list(),
						controllers.sra.configurations.api.routes.javascript.Configurations.get(),
						controllers.sra.configurations.tpl.routes.javascript.Configurations.get(),
						controllers.sra.configurations.api.routes.javascript.Configurations.list(),
						controllers.sra.configurations.api.routes.javascript.Configurations.update());
	}
	
	/*
	public Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(  	    		
				//Routes.javascriptRouter("jsRoutes",
				JavaScriptReverseRouter.create("jsRoutes",
						// Routes
						controllers.sra.configurations.tpl.routes.javascript.Configurations.home(),
						controllers.projects.api.routes.javascript.Projects.list(),
						controllers.commons.api.routes.javascript.States.list(),
						controllers.sra.configurations.api.routes.javascript.Configurations.save(),
						controllers.sra.api.routes.javascript.Variables.get(),
						controllers.sra.api.routes.javascript.Variables.list(),
						controllers.sra.configurations.api.routes.javascript.Configurations.get(),
						controllers.sra.configurations.tpl.routes.javascript.Configurations.get(),
						controllers.sra.configurations.api.routes.javascript.Configurations.list(),
						controllers.sra.configurations.api.routes.javascript.Configurations.update()	
						)
				);
	}
	*/

  	 
}
