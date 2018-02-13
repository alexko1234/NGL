package controllers.archives.tpl;

// import play.Routes;
//import play.routing.JavaScriptReverseRouter;
import play.mvc.Result;

import views.html.archives.home;
import views.html.archives.search;

import javax.inject.Inject;

import fr.cea.ig.authentication.Authenticated;
import fr.cea.ig.authorization.Authorized;
import fr.cea.ig.lfw.Historized;
import fr.cea.ig.ngl.NGLApplication;
import fr.cea.ig.ngl.NGLController;
import fr.cea.ig.ngl.support.NGLJavascript;
import fr.cea.ig.play.NGLContext;

/**
 * Controller around archive readset object
 * @author galbini
 *
 */
// import controllers.CommonController;
// public class ReadSets extends CommonController {
public class ReadSets extends NGLController
                     implements NGLJavascript {

	private final home home;
	private final search search;
	
	@Inject
	public ReadSets(NGLApplication app, home home, search search) {
		super(app);
		this.home   = home;
		this.search = search;
	}
	
	@Authenticated
	@Historized
	@Authorized.Read
	public Result home(String homecode) {
		return ok(home.render(homecode));
	}

	public Result get(String code) {
		return ok(home.render(code));
	}

	public Result search() {		
		return ok(search.render());
	}

	public Result javascriptRoutes() {
		return jsRoutes(controllers.archives.tpl.routes.javascript.ReadSets.home(),  
						controllers.archives.tpl.routes.javascript.ReadSets.get(),  
						controllers.archives.api.routes.javascript.ReadSets.list());	  	      
	}
	
	/*
	public Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(  	    		
				//Routes.javascriptRouter("jsRoutes",
				JavaScriptReverseRouter.create("jsRoutes",
						// Routes
						controllers.archives.tpl.routes.javascript.ReadSets.home(),  
						controllers.archives.tpl.routes.javascript.ReadSets.get(),  
						controllers.archives.api.routes.javascript.ReadSets.list()  	    		
						)	  	      
				);
	}
*/
}
