package controllers.archives.tpl;

// import play.Routes;
import play.routing.JavaScriptReverseRouter;
import play.mvc.Result;
import views.html.archives.home;
import views.html.archives.search;

import javax.inject.Inject;

import controllers.CommonController;
/**
 * Controller around archive readset object
 * @author galbini
 *
 */
public class ReadSets extends CommonController {

	private final home home;
	private final search search;
	@Inject
	public ReadSets(home home, search search) {
		this.home   = home;
		this.search = search;
	}
	
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

}
