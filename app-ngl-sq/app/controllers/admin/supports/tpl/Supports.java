package controllers.admin.supports.tpl;

//import play.Routes;
import play.routing.JavaScriptReverseRouter;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import views.html.admin.supports.*;
import controllers.APICommonController;
import controllers.CommonController;
import controllers.history.UserHistory;

import javax.inject.Inject;

// TODO: cleanup and comment

// public class Supports extends CommonController {
@With({fr.cea.ig.authentication.Authenticate.class, UserHistory.class})
public class Supports extends Controller {

	private final home home;
	
	private final searchSwitchIndex searchSwitchIndex;
	
	@Inject
	public Supports(home home, searchSwitchIndex searchSwitchIndex) {
		this.home              = home;
		this.searchSwitchIndex = searchSwitchIndex;
	}
	
	public Result home(String code) {
		return ok(home.render(code));
	}
	
	public Result search(String code) {
		if ("switch-index".equals(code)) {
			return ok(searchSwitchIndex.render());
		} else if("content-update".equals(code)) {
			return ok(contentUpdate.render());
		} else {
			return ok();
		}
	}

	public Result javascriptRoutes() {
		//response().setContentType("text/javascript");
		return ok(  	    		
				JavaScriptReverseRouter.create("jsRoutes",
						// Routes
						controllers.projects.api.routes.javascript.Projects.list(),
						controllers.samples.api.routes.javascript.Samples.list(),
						controllers.containers.api.routes.javascript.Containers.list(),
						controllers.containers.api.routes.javascript.ContainerSupports.list(),
						controllers.experiments.api.routes.javascript.Experiments.list(),
						controllers.commons.api.routes.javascript.States.list(),
						controllers.readsets.api.routes.javascript.ReadSets.list(),
						controllers.commons.api.routes.javascript.Users.list(),
						controllers.commons.api.routes.javascript.Values.list(),
						controllers.commons.api.routes.javascript.Parameters.list(),
						controllers.commons.api.routes.javascript.PropertyDefinitions.list(),
						controllers.admin.supports.tpl.routes.javascript.Supports.home(),
						controllers.admin.supports.tpl.routes.javascript.Supports.search(),
						controllers.admin.supports.api.routes.javascript.NGLObjects.list(),
						controllers.admin.supports.api.routes.javascript.NGLObjects.update()
						)  	      
				).as("text/javascript");
	}
	
}
