package controllers.barcodes.tpl;

import javax.inject.Inject;

import controllers.CommonController;

import play.routing.JavaScriptReverseRouter;
import play.mvc.Result;

import views.html.barcodes.home;
import views.html.barcodes.create;
import views.html.barcodes.search;

public class Barcodes extends CommonController {
	
	private home home;
	private create create;
	private search search;
	
	@Inject
	public Barcodes(home home, create create, search search) {
		this.home = home;
		this.create = create;
		this.search = search;
	}
	
	public Result home(String homecode) {
		return ok(home.render(homecode));
	}

	public Result create() {
		return ok(create.render());
	}

	public Result search() {
		return ok(search.render());
	}

	public Result javascriptRoutes() {
		return jsRoutes(controllers.barcodes.tpl.routes.javascript.Barcodes.home() ,
						controllers.barcodes.api.routes.javascript.Barcodes.list(),
						controllers.barcodes.api.routes.javascript.Barcodes.save(),
						controllers.barcodes.api.routes.javascript.Barcodes.delete(),
						controllers.combo.api.routes.javascript.Lists.projects(),
						controllers.combo.api.routes.javascript.Lists.etmanips());
	}
	
	/*public Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(  	    		
				//Routes.javascriptRouter("jsRoutes",
				JavaScriptReverseRouter.create("jsRoutes",
						// Routes
						controllers.barcodes.tpl.routes.javascript.Barcodes.home() ,
						controllers.barcodes.api.routes.javascript.Barcodes.list(),
						controllers.barcodes.api.routes.javascript.Barcodes.save(),
						controllers.barcodes.api.routes.javascript.Barcodes.delete(),
						controllers.combo.api.routes.javascript.Lists.projects(),
						controllers.combo.api.routes.javascript.Lists.etmanips()
						)	  	      
				);
	}*/
	
}
