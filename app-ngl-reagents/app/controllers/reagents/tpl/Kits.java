package controllers.reagents.tpl;

import controllers.CommonController;
import play.Routes;
import play.mvc.Result;
import views.html.declarations.home;
import views.html.declarations.kitsCreation;
import views.html.declarations.kitsSearch;

public class Kits extends CommonController {
	public Result home(String code){
		return ok(home.render(code));
	}
	
	public Result search(){
		return ok(kitsSearch.render());
	}
	
	
	public Result get(String code){
		return ok(home.render(code));
	}
	
	public Result createOrEdit(){
		return ok(kitsCreation.render());
	}
	
	public Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(	  	      
				Routes.javascriptRouter("jsRoutes",  	       
						// Routes	
						controllers.reagents.tpl.routes.javascript.Kits.createOrEdit(),
						controllers.reagents.tpl.routes.javascript.Kits.get(),
						controllers.reagents.api.routes.javascript.Kits.save(),
						controllers.reagents.api.routes.javascript.KitCatalogs.list(),
						controllers.reagents.api.routes.javascript.BoxCatalogs.list(),
						controllers.reagents.api.routes.javascript.ReagentCatalogs.list(),
						controllers.reagents.api.routes.javascript.Kits.delete(),
						controllers.reagents.api.routes.javascript.Kits.list(),
						controllers.reagents.api.routes.javascript.Boxes.list(),
						controllers.reagents.api.routes.javascript.Boxes.save(),
						controllers.reagents.api.routes.javascript.Boxes.update(),
						controllers.reagents.api.routes.javascript.Boxes.delete(),
						controllers.reagents.api.routes.javascript.Kits.update(),
						controllers.reagents.api.routes.javascript.Kits.get(),
						controllers.reagents.tpl.routes.javascript.Kits.home(),
						controllers.reagents.tpl.routes.javascript.Kits.search(),
						controllers.reagents.api.routes.javascript.Reagents.save(),
						controllers.reagents.api.routes.javascript.Reagents.update(),
						controllers.reagents.api.routes.javascript.Reagents.delete(),
						controllers.reagents.api.routes.javascript.Reagents.list(),
		  	    		controllers.experiments.api.routes.javascript.ExperimentTypes.list(),
		  	    		controllers.commons.api.routes.javascript.States.list()
						)
				);
	}
}
