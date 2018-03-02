package controllers.containers.tpl;

import java.util.ArrayList;
import java.util.List;

// import play.Routes;
//import play.routing.JavaScriptReverseRouter;

import play.i18n.Messages;
import play.mvc.Result;
import views.html.container.*;

import javax.inject.Inject;

import fr.cea.ig.authentication.Authenticated;
import fr.cea.ig.authorization.Authorized;
import fr.cea.ig.lfw.Historized;
import fr.cea.ig.ngl.NGLApplication;
import fr.cea.ig.ngl.NGLController;
import fr.cea.ig.ngl.support.NGLJavascript;
// import controllers.NGLBaseController;
import fr.cea.ig.play.NGLContext;

// TODO: clean, comment

//import controllers.CommonController;
// public class Containers extends -CommonController {
public class Containers extends NGLController implements NGLJavascript { // NGLBaseController {

	private final home        home;
	private final search      search;
	private final newFromFile newFromFile;
	private final details     details;
	
	@Inject
	public Containers(NGLApplication app, home home, search search, newFromFile newFromFile, details details) {
		super(app);
		this.home        = home;
		this.search      = search;
		this.newFromFile = newFromFile;
		this.details     = details;
	}
	
	@Authenticated
	@Historized
	@Authorized.Read
    public Result home(String code) {
        return ok(home.render(code));
    }

	@Authenticated
	@Historized
	@Authorized.Read
    public Result get(String code) {
        return ok(home.render("search"));
    }

	// tpl
    public Result search() {
        return ok(search.render());
    }

    // tpl
    public Result newFromFile() {
        return ok(newFromFile.render());
    }

    // tpl
    public Result details() {
        return ok(details.render());
    }

    // tpl
    public Result javascriptRoutes() {
        return jsRoutes(controllers.projects.api.routes.javascript.Projects.list(),
                		controllers.containers.tpl.routes.javascript.Containers.get(),
                		controllers.containers.tpl.routes.javascript.Containers.details(),
                		controllers.containers.tpl.routes.javascript.ContainerSupports.get(),
                		controllers.containers.tpl.routes.javascript.Containers.newFromFile(),
                		controllers.printing.tpl.routes.javascript.Printing.home(),
                		controllers.containers.api.routes.javascript.Containers.get(),
                		controllers.containers.api.routes.javascript.Containers.update(),
                		controllers.samples.api.routes.javascript.Samples.list(),
                		controllers.containers.api.routes.javascript.Containers.list(),
                		controllers.experiments.api.routes.javascript.ExperimentTypes.list(),
                		controllers.containers.api.routes.javascript.ContainerSupportCategories.list(),
                		controllers.commons.api.routes.javascript.States.list(),
                		controllers.commons.api.routes.javascript.PropertyDefinitions.list(),
                		controllers.processes.api.routes.javascript.ProcessTypes.list(),
                		controllers.processes.api.routes.javascript.ProcessCategories.list(),
                		controllers.containers.api.routes.javascript.ContainerCategories.list(),
                		controllers.containers.tpl.routes.javascript.Containers.search(),
                		controllers.containers.tpl.routes.javascript.Containers.home(),
                		controllers.containers.api.routes.javascript.ContainerSupports.list(),
                		controllers.containers.api.routes.javascript.ContainerSupports.saveCode(),
                		controllers.containers.api.routes.javascript.Containers.updateStateBatch(),
                		controllers.reporting.api.routes.javascript.ReportingConfigurations.list(),
                		controllers.reporting.api.routes.javascript.ReportingConfigurations.get(),
                		controllers.reporting.api.routes.javascript.ReportingConfigurations.save(),
                		controllers.reporting.api.routes.javascript.ReportingConfigurations.update(),
                		controllers.commons.api.routes.javascript.Users.list(),
                		controllers.commons.api.routes.javascript.PropertyDefinitions.list(),
                		controllers.reporting.api.routes.javascript.ReportingConfigurations.delete(),
                		controllers.reporting.api.routes.javascript.FilteringConfigurations.list(),
                		controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
                		controllers.commons.api.routes.javascript.Values.list(),
                		controllers.commons.api.routes.javascript.Parameters.list(),
                		controllers.experiments.tpl.routes.javascript.Experiments.get(),
                		controllers.receptions.api.routes.javascript.ReceptionConfigurations.list(),
                		controllers.resolutions.api.routes.javascript.Resolutions.list(),
                		controllers.receptions.io.routes.javascript.Receptions.importFile(),
                		controllers.printing.api.routes.javascript.Tags.list(),
                		controllers.printing.api.routes.javascript.Tags.print(),
                		controllers.printing.tpl.routes.javascript.Printing.home(),
                		controllers.commons.api.routes.javascript.Parameters.list());
    }
    
    /*public Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(
        	JavaScriptReverseRouter.create("jsRoutes", 
            // Routes
                controllers.projects.api.routes.javascript.Projects.list(),
                controllers.containers.tpl.routes.javascript.Containers.get(),
                controllers.containers.tpl.routes.javascript.Containers.details(),
                controllers.containers.tpl.routes.javascript.ContainerSupports.get(),
                controllers.containers.tpl.routes.javascript.Containers.newFromFile(),
                controllers.printing.tpl.routes.javascript.Printing.home(),
                controllers.containers.api.routes.javascript.Containers.get(),
                controllers.containers.api.routes.javascript.Containers.update(),
                controllers.samples.api.routes.javascript.Samples.list(),
                controllers.containers.api.routes.javascript.Containers.list(),
                controllers.experiments.api.routes.javascript.ExperimentTypes.list(),
                controllers.containers.api.routes.javascript.ContainerSupportCategories.list(),
                controllers.commons.api.routes.javascript.States.list(),
                controllers.commons.api.routes.javascript.PropertyDefinitions.list(),
                controllers.processes.api.routes.javascript.ProcessTypes.list(),
                controllers.processes.api.routes.javascript.ProcessCategories.list(),
                controllers.containers.api.routes.javascript.ContainerCategories.list(),
                controllers.containers.tpl.routes.javascript.Containers.search(),
                controllers.containers.tpl.routes.javascript.Containers.home(),
                controllers.containers.api.routes.javascript.ContainerSupports.list(),
                controllers.containers.api.routes.javascript.ContainerSupports.saveCode(),
                controllers.containers.api.routes.javascript.Containers.updateStateBatch(),
                controllers.reporting.api.routes.javascript.ReportingConfigurations.list(),
                controllers.reporting.api.routes.javascript.ReportingConfigurations.get(),
                controllers.reporting.api.routes.javascript.ReportingConfigurations.save(),
                controllers.reporting.api.routes.javascript.ReportingConfigurations.update(),
                controllers.commons.api.routes.javascript.Users.list(),
                controllers.commons.api.routes.javascript.PropertyDefinitions.list(),
                controllers.reporting.api.routes.javascript.ReportingConfigurations.delete(),
                controllers.reporting.api.routes.javascript.FilteringConfigurations.list(),
                controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
                controllers.commons.api.routes.javascript.Values.list(),
                controllers.commons.api.routes.javascript.Parameters.list(),
                controllers.experiments.tpl.routes.javascript.Experiments.get(),
                controllers.receptions.api.routes.javascript.ReceptionConfigurations.list(),
                controllers.resolutions.api.routes.javascript.Resolutions.list(),
                controllers.receptions.io.routes.javascript.Receptions.importFile(),
	      		controllers.printing.api.routes.javascript.Tags.list(),
                controllers.printing.api.routes.javascript.Tags.print(),
  	    		controllers.printing.tpl.routes.javascript.Printing.home(),
                controllers.commons.api.routes.javascript.Parameters.list()
          )
        );
    }*/
    
}
