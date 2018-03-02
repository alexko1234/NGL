package controllers.processes.tpl;

//import models.laboratory.processes.description.ProcessType;
//import models.utils.dao.DAOException;
//import play.Logger;
//import play.Routes;
// import play.routing.JavaScriptReverseRouter;

//import play.libs.Json;
import play.mvc.Result;
import views.html.processes.home;
import views.html.processes.newProcesses;
import views.html.processes.assignProcesses;
import views.html.processes.search;
import views.html.processes.searchContainers;
import views.html.processes.searchSamples;
import javax.inject.Inject;

import fr.cea.ig.authentication.Authenticated;
import fr.cea.ig.authorization.Authorized;
import fr.cea.ig.lfw.Historized;
import fr.cea.ig.ngl.NGLApplication;
import fr.cea.ig.ngl.NGLController;
import fr.cea.ig.ngl.support.NGLJavascript;
// import controllers.NGLBaseController;
import fr.cea.ig.play.NGLContext;

// import controllers.CommonController;              // done
// public class Processes extends -CommonController{ // done

public class Processes extends NGLController implements NGLJavascript { // NGLBaseController {
	
	private final home home;
	private final searchContainers searchContainers;
	private final searchSamples searchSamples;
	
	private final search search;
	private final newProcesses newProcesses;
	private final assignProcesses assignProcesses;
	
	@Inject
	public Processes(NGLApplication app, home home, searchContainers searchContainers, search search, newProcesses newProcesses) {
		super(app);
		this.home = home;
		this.searchContainers = searchContainers;
		this.searchSamples = searchSamples;		
		this.search = search;
		this.newProcesses = newProcesses;
		this.assignProcesses = assignProcesses;
	}
	
	public Result home(String code){
		return ok(home.render(code));
	}

	// tpl
	public Result searchContainers() {
		return ok(searchContainers.render());
	}

	// tpl
	public Result search(String processTypeCode) {
		return ok(search.render());
	}

	// tpl
	public Result newProcesses(String processTypeCode) {
		return ok(newProcesses.render());
	}

	// tpl
	public Result javascriptRoutes() {
		return jsRoutes(controllers.processes.tpl.routes.javascript.Processes.newProcesses(),  
						controllers.processes.tpl.routes.javascript.Processes.search(),
						controllers.processes.tpl.routes.javascript.Processes.searchContainers(),
						controllers.processes.tpl.routes.javascript.Processes.home(),  
						controllers.processes.api.routes.javascript.Processes.update(),
						controllers.processes.api.routes.javascript.Processes.save(),
						controllers.processes.api.routes.javascript.Processes.saveBatch(),
						controllers.processes.api.routes.javascript.Processes.delete(),
						controllers.processes.api.routes.javascript.Processes.updateState(),
						controllers.processes.api.routes.javascript.ProcessTypes.list(),
						controllers.processes.api.routes.javascript.ProcessTypes.get(),
						controllers.containers.api.routes.javascript.Containers.list(),
						controllers.processes.api.routes.javascript.Processes.list(),
						controllers.processes.api.routes.javascript.ProcessCategories.list(),
						controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
						controllers.commons.api.routes.javascript.Values.list(),
						controllers.projects.api.routes.javascript.Projects.list(),
						controllers.samples.api.routes.javascript.Samples.list(),
						controllers.experiments.api.routes.javascript.Experiments.list(),
						controllers.containers.api.routes.javascript.ContainerSupports.list(),
						controllers.commons.api.routes.javascript.States.list(),
						controllers.commons.api.routes.javascript.Users.list(),
						controllers.reporting.api.routes.javascript.FilteringConfigurations.list(),
						controllers.containers.api.routes.javascript.ContainerSupportCategories.list(),
						controllers.experiments.api.routes.javascript.ExperimentTypes.list(),
						controllers.experiments.api.routes.javascript.ExperimentTypes.getDefaultFirstExperiments(),
						controllers.reporting.api.routes.javascript.ReportingConfigurations.list(),
			      		controllers.reporting.api.routes.javascript.ReportingConfigurations.get(),
			      		controllers.reporting.api.routes.javascript.ReportingConfigurations.save(),
			      		controllers.reporting.api.routes.javascript.ReportingConfigurations.update(),
			      		controllers.reporting.api.routes.javascript.ReportingConfigurations.delete(),
			      		controllers.commons.api.routes.javascript.Values.list(),
			      		controllers.commons.api.routes.javascript.Parameters.list(),
			      		controllers.resolutions.api.routes.javascript.Resolutions.list());
	}

/*	public Result javascriptRoutes() {
	public Result assignProcesses(String processTypeCode){
		return ok(assignProcesses.render());
	}
	
	public Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(  	    		
				// Routes.javascriptRouter("jsRoutes",
			JavaScriptReverseRouter.create("jsRoutes",
						// Routes
						controllers.processes.tpl.routes.javascript.Processes.newProcesses(),
						controllers.processes.tpl.routes.javascript.Processes.assignProcesses(),  
						controllers.processes.tpl.routes.javascript.Processes.search(),
						controllers.processes.tpl.routes.javascript.Processes.searchContainers(),
						controllers.processes.tpl.routes.javascript.Processes.searchSamples(),
						controllers.processes.tpl.routes.javascript.Processes.home(),  
						controllers.processes.api.routes.javascript.Processes.update(),
						controllers.processes.api.routes.javascript.Processes.updateBatch(),
						controllers.processes.api.routes.javascript.Processes.save(),
						controllers.processes.api.routes.javascript.Processes.saveBatch(),
						controllers.processes.api.routes.javascript.Processes.delete(),
						controllers.processes.api.routes.javascript.Processes.updateState(),
						controllers.processes.api.routes.javascript.ProcessTypes.list(),
						controllers.processes.api.routes.javascript.ProcessTypes.get(),
						controllers.containers.api.routes.javascript.Containers.list(),
						controllers.processes.api.routes.javascript.Processes.list(),
						controllers.processes.api.routes.javascript.ProcessCategories.list(),
						controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
						controllers.commons.api.routes.javascript.Values.list(),
						controllers.projects.api.routes.javascript.Projects.list(),
						controllers.samples.api.routes.javascript.Samples.list(),
						controllers.experiments.api.routes.javascript.Experiments.list(),
						controllers.containers.api.routes.javascript.ContainerSupports.list(),
						controllers.commons.api.routes.javascript.States.list(),
						controllers.commons.api.routes.javascript.Users.list(),
						controllers.reporting.api.routes.javascript.FilteringConfigurations.list(),
						controllers.containers.api.routes.javascript.ContainerSupportCategories.list(),
						controllers.experiments.api.routes.javascript.ExperimentTypes.list(),
						controllers.experiments.api.routes.javascript.ExperimentTypes.getDefaultFirstExperiments(),
						controllers.reporting.api.routes.javascript.ReportingConfigurations.list(),
			      		controllers.reporting.api.routes.javascript.ReportingConfigurations.get(),
			      		controllers.reporting.api.routes.javascript.ReportingConfigurations.save(),
			      		controllers.reporting.api.routes.javascript.ReportingConfigurations.update(),
			      		controllers.reporting.api.routes.javascript.ReportingConfigurations.delete(),
			      		controllers.commons.api.routes.javascript.Values.list(),
			      		controllers.commons.api.routes.javascript.Parameters.list(),
			      		controllers.resolutions.api.routes.javascript.Resolutions.list(),
			      		controllers.containers.api.routes.javascript.ContainerCategories.list(),
			      		controllers.experiments.api.routes.javascript.ExperimentTypeNodes.get(),
		  	    		
						controllers.protocols.api.routes.javascript.Protocols.list()
						)	  	      
				);
	}*/
	
}
