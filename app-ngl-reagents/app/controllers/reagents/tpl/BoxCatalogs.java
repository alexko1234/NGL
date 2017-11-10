package controllers.reagents.tpl;

import javax.inject.Inject;

import controllers.CommonController;
//import play.Routes;
import play.mvc.Result;
import views.html.catalogs.home;
import views.html.catalogs.kitCatalogsCreation;
import views.html.catalogs.kitCatalogsSearch;
import play.routing.JavaScriptReverseRouter;

public class BoxCatalogs extends CommonController {

	private final home home;
	
	@Inject
	public BoxCatalogs(home home) {
		this.home = home;
	}
	public Result home(String code){
		return ok(home.render(code));
	}

	public Result get(String code){
		return ok(home.render(code));
	}
	
	public Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(	  	      
				// Routes.javascriptRouter("jsRoutes",
				JavaScriptReverseRouter.create("jsRoutes",
						// Routes	
						controllers.reagents.api.routes.javascript.BoxCatalogs.list()
						)
				);
	}

}
