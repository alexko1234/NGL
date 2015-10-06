package controllers.reagents.tpl;

import controllers.CommonController;
import play.Routes;
import play.mvc.Result;
import views.html.catalogs.home;
import views.html.catalogs.kitCatalogsCreation;
import views.html.catalogs.kitCatalogsSearch;

public class BoxCatalogs extends CommonController {

	public Result home(String code){
		return ok(home.render(code));
	}

	public Result get(String code){
		return ok(home.render(code));
	}
	
	public Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(	  	      
				Routes.javascriptRouter("jsRoutes",  	       
						// Routes	
						controllers.reagents.api.routes.javascript.BoxCatalogs.list()
						)
				);
	}

}
