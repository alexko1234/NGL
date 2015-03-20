package controllers.reagents.tpl;

import play.Routes;
import play.mvc.Result;
import views.html.catalogs.home;
import views.html.catalogs.kitCatalogsCreation;
import views.html.catalogs.kitCatalogsSearch;
import controllers.CommonController;

public class KitCatalogs extends CommonController{

	public Result home(String code){
		return ok(home.render(code));
	}
	
	public Result search(){
		return ok(kitCatalogsSearch.render());
	}
	
	
	public Result get(String code){
		return ok(home.render(code));
	}
	
	public Result createOrEdit(){
		return ok(kitCatalogsCreation.render());
	}
	
	public Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(	  	      
				Routes.javascriptRouter("jsRoutes",  	       
						// Routes	
						controllers.reagents.tpl.routes.javascript.KitCatalogs.createOrEdit(),
						controllers.reagents.tpl.routes.javascript.KitCatalogs.get(),
						controllers.reagents.api.routes.javascript.KitCatalogs.save(),
						controllers.reagents.api.routes.javascript.KitCatalogs.delete(),
						controllers.reagents.api.routes.javascript.KitCatalogs.list(),
						controllers.reagents.api.routes.javascript.BoxCatalogs.list(),
						controllers.reagents.api.routes.javascript.BoxCatalogs.save(),
						controllers.reagents.api.routes.javascript.BoxCatalogs.update(),
						controllers.reagents.api.routes.javascript.BoxCatalogs.delete(),
						controllers.reagents.api.routes.javascript.KitCatalogs.update(),
						controllers.reagents.api.routes.javascript.KitCatalogs.get(),
						controllers.reagents.tpl.routes.javascript.KitCatalogs.home(),
						controllers.reagents.tpl.routes.javascript.KitCatalogs.search(),
						controllers.reagents.api.routes.javascript.ReagentCatalogs.save(),
						controllers.reagents.api.routes.javascript.ReagentCatalogs.update(),
						controllers.reagents.api.routes.javascript.ReagentCatalogs.delete(),
						controllers.reagents.api.routes.javascript.ReagentCatalogs.list(),
		  	    		controllers.experiments.api.routes.javascript.ExperimentTypes.list()
						)
				);
	}
}
