package controllers.samples.tpl;

import models.laboratory.processes.description.ProcessType;
import models.utils.dao.DAOException;
import play.Logger;
// import play.Routes;
import play.libs.Json;
import play.mvc.Result;
import views.html.samples.details;
import views.html.samples.home;
import views.html.samples.search;

import javax.inject.Inject;

import fr.cea.ig.ngl.NGLApplication;
import fr.cea.ig.ngl.NGLController;
import fr.cea.ig.ngl.support.NGLJavascript;
// import controllers.NGLBaseController;
import fr.cea.ig.play.NGLContext;
// import controllers.CommonController;
import play.routing.JavaScriptReverseRouter;

// TODO: clean, comment

//import controllers.CommonController;             // done
// public class Samples extends -CommonController{ // done

public class Samples extends NGLController 
                    implements NGLJavascript  { // NGLBaseController {

	private final home home;
	private final search search;
	private final details details;
	
//	@Inject
//	public Samples(NGLContext ctx, home home, search search, details details) {
//		super(ctx);
//		this.home = home;
//		this.search = search;
//		this.details = details;
//	}

	@Inject
	public Samples(NGLApplication app, home home, search search, details details) {
		super(app);
		this.home = home;
		this.search = search;
		this.details = details;
	}
	
	public Result home(String code) {
		return ok(home.render(code));
	}

	public Result search() {
		return ok(search.render());
	}

	public Result get(String code) {
		return ok(home.render("search"));
	}

	public Result details() {
		return ok(details.render());
	}

	public Result javascriptRoutes() {
		return jsRoutes(controllers.processes.api.routes.javascript.ProcessTypes.list(),
						controllers.containers.api.routes.javascript.Containers.list(),
						controllers.processes.api.routes.javascript.Processes.list(),
						controllers.processes.api.routes.javascript.ProcessCategories.list(),
						controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
						controllers.commons.api.routes.javascript.Values.list(),
						controllers.projects.api.routes.javascript.Projects.list(),

						controllers.samples.api.routes.javascript.Samples.list(),
						controllers.samples.api.routes.javascript.Samples.get(),
						controllers.samples.api.routes.javascript.Samples.update(),

						controllers.samples.tpl.routes.javascript.Samples.get(),
						controllers.samples.tpl.routes.javascript.Samples.search(),
						controllers.samples.tpl.routes.javascript.Samples.details(),
						controllers.samples.tpl.routes.javascript.Samples.home(),


						controllers.containers.api.routes.javascript.Containers.get(),

						controllers.experiments.api.routes.javascript.Experiments.list(),
						controllers.commons.api.routes.javascript.States.list(),
						controllers.commons.api.routes.javascript.Users.list(),
						controllers.reporting.api.routes.javascript.FilteringConfigurations.list(),
						controllers.containers.api.routes.javascript.ContainerSupportCategories.list(),
						controllers.experiments.api.routes.javascript.ExperimentTypes.list(),
						controllers.reporting.api.routes.javascript.ReportingConfigurations.list(),
						controllers.reporting.api.routes.javascript.ReportingConfigurations.get(),			      		
						controllers.commons.api.routes.javascript.Values.list(),
						controllers.commons.api.routes.javascript.Parameters.list(),
						controllers.resolutions.api.routes.javascript.Resolutions.list(),
						controllers.commons.api.routes.javascript.PropertyDefinitions.list(),
						controllers.processes.tpl.routes.javascript.Processes.home(),
						controllers.experiments.tpl.routes.javascript.Experiments.get(),

						controllers.protocols.api.routes.javascript.Protocols.list());
	}

	/*
	public Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(  	    		
				// Routes.javascriptRouter("jsRoutes",
				JavaScriptReverseRouter.create("jsRoutes",
						// Routes
						controllers.processes.api.routes.javascript.ProcessTypes.list(),
						controllers.containers.api.routes.javascript.Containers.list(),
						controllers.processes.api.routes.javascript.Processes.list(),
						controllers.processes.api.routes.javascript.ProcessCategories.list(),
						controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
						controllers.commons.api.routes.javascript.Values.list(),
						controllers.projects.api.routes.javascript.Projects.list(),

						controllers.samples.api.routes.javascript.Samples.list(),
						controllers.samples.api.routes.javascript.Samples.get(),
						controllers.samples.api.routes.javascript.Samples.update(),

						controllers.samples.tpl.routes.javascript.Samples.get(),
						controllers.samples.tpl.routes.javascript.Samples.search(),
						controllers.samples.tpl.routes.javascript.Samples.details(),
						controllers.samples.tpl.routes.javascript.Samples.home(),


						controllers.containers.api.routes.javascript.Containers.get(),

						controllers.experiments.api.routes.javascript.Experiments.list(),
						controllers.commons.api.routes.javascript.States.list(),
						controllers.commons.api.routes.javascript.Users.list(),
						controllers.reporting.api.routes.javascript.FilteringConfigurations.list(),
						controllers.containers.api.routes.javascript.ContainerSupportCategories.list(),
						controllers.experiments.api.routes.javascript.ExperimentTypes.list(),
						controllers.reporting.api.routes.javascript.ReportingConfigurations.list(),
						controllers.reporting.api.routes.javascript.ReportingConfigurations.get(),			      		
						controllers.commons.api.routes.javascript.Values.list(),
						controllers.commons.api.routes.javascript.Parameters.list(),
						controllers.resolutions.api.routes.javascript.Resolutions.list(),
						controllers.commons.api.routes.javascript.PropertyDefinitions.list(),
						controllers.processes.tpl.routes.javascript.Processes.home(),
						controllers.experiments.tpl.routes.javascript.Experiments.get(),

						controllers.protocols.api.routes.javascript.Protocols.list()
						)	  	      
				);
	}
	*/
}
