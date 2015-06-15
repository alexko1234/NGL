package controllers.barcodes.tpl;

import java.util.ArrayList;
import java.util.List;

import controllers.CommonController;
import controllers.plates.tpl.routes.javascript;
import play.Routes;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;

public class Barcodes extends CommonController {
	
		public static Result home(String homecode) {
			return ok(views.html.barcodes.home.render(homecode));
		}

		public static Result create() {
			return ok(views.html.barcodes.create.render());
		}
		
		public static Result search() {
			return ok(views.html.barcodes.search.render());
		}
		
	
		
		public static Result javascriptRoutes() {
	  	    response().setContentType("text/javascript");
	  	    return ok(  	    		
	  	      Routes.javascriptRouter("jsRoutes",
	  	        // Routes
	  	    		controllers.barcodes.tpl.routes.javascript.Barcodes.home() ,
	  	    		controllers.barcodes.api.routes.javascript.Barcodes.list(),
	  	    		controllers.barcodes.api.routes.javascript.Barcodes.save(),
	  	    		controllers.barcodes.api.routes.javascript.Barcodes.delete(),
	  	    		controllers.combo.api.routes.javascript.Lists.projects(),
	  	    		controllers.combo.api.routes.javascript.Lists.etmanips()
	  	      )	  	      
	  	    );
	  	  }
}
