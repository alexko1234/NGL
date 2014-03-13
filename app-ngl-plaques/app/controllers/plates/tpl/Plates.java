package controllers.plates.tpl;

import java.util.ArrayList;
import java.util.List;

import controllers.CommonController;
import controllers.plates.tpl.routes.javascript;
import play.Routes;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;

public class Plates extends CommonController {
	
		public static Result home(String homecode) {
			return ok(views.html.plates.home.render(homecode));
		}

		public static Result get(String code) {
			return ok(views.html.plates.home.render("search"));
		}
		
		public static Result searchManips() {
			return ok(views.html.plates.searchManips.render());
		}
		
		public static Result search(){
			return ok(views.html.plates.search.render());
		}
		
		//TODO messages Code,Nom
		public static Result details(){
			return ok(views.html.plates.details.render());
		}
		
		public static Result javascriptRoutes() {
	  	    response().setContentType("text/javascript");
	  	    return ok(  	    		
	  	      Routes.javascriptRouter("jsRoutes",
	  	        // Routes
	  	    		controllers.plates.tpl.routes.javascript.Plates.home() ,
	  	    		controllers.plates.tpl.routes.javascript.Plates.get(),
	  	    		controllers.plates.tpl.routes.javascript.Plates.details() ,
	  	    		controllers.plates.api.routes.javascript.Plates.list(),
	  	    		controllers.plates.api.routes.javascript.Plates.get(),
	  	    		controllers.plates.api.routes.javascript.Plates.save(),
	  	    		controllers.plates.api.routes.javascript.Plates.remove(),
	  	    		controllers.manips.api.routes.javascript.Manips.list(),
	  	    		controllers.combo.api.routes.javascript.Lists.projects(),
	  	    		controllers.combo.api.routes.javascript.Lists.etmateriels(),
	  	    		controllers.combo.api.routes.javascript.Lists.etmanips()
	  	      )	  	      
	  	    );
	  	  }
}
