package controllers.plaques.tpl;

import java.util.ArrayList;
import java.util.List;

import controllers.CommonController;
import play.Routes;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;

public class Plaques extends CommonController {
	
		public static Result home(String code) {
			return ok(views.html.plaques.home.render(code));
		}

		public static Result searchManips() {
			List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
			columns.add(DatatableHelpers.getColumn("matmaco", "manips.table.code", true, false, false));
			columns.add(DatatableHelpers.getColumn("matmanom", "manips.table.name", true, false, false));
			DatatableConfig config = new DatatableConfig(columns);
			config.button = Boolean.TRUE;
			return ok(views.html.plaques.searchManips.render(config));
		}
		
		public static Result search(){
			List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
			columns.add(DatatableHelpers.getColumn("code", "plates.table.code", true, false, false));
			columns.add(DatatableHelpers.getColumn("nbWells", "plates.table.nbWells"));
			DatatableConfig config = new DatatableConfig(columns);
			config.show = true;
			config.button = true;
			return ok(views.html.plaques.search.render(config));
		}
		
		//TODO messages Code,Nom
		public static Result details(){
			List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
			columns.add(DatatableHelpers.getColumn("code", "plates.table.well.code", true, false, false));
			columns.add(DatatableHelpers.getColumn("name", "plates.table.well.name", true, false, false));		
			columns.add(DatatableHelpers.getColumn("x", "plates.table.well.x", true, true, false));
			columns.add(DatatableHelpers.getColumn("y", "plates.table.well.y", true, true, false));		
			DatatableConfig config = new DatatableConfig(columns);
			config.remove=true;
			config.button=true;		
			config.editColumn=false;
			return ok(views.html.plaques.details.render(config));
		}
		
		public static Result javascriptRoutes() {
	  	    response().setContentType("text/javascript");
	  	    return ok(  	    		
	  	      Routes.javascriptRouter("jsRoutes",
	  	        // Routes
	  	    		controllers.plaques.tpl.routes.javascript.Plaques.home() ,
	  	    		controllers.plaques.tpl.routes.javascript.Plaques.details() ,
	  	    		controllers.plaques.api.routes.javascript.Plaques.list(),
	  	    		controllers.plaques.api.routes.javascript.Plaques.get(),
	  	    		controllers.plaques.api.routes.javascript.Plaques.save(),
	  	    		controllers.manips.api.routes.javascript.Manips.list(),
	  	    		controllers.lists.api.routes.javascript.Lists.projects(),
	  	    		controllers.lists.api.routes.javascript.Lists.etmateriels(),
	  	    		controllers.lists.api.routes.javascript.Lists.etmanips()
	  	      )	  	      
	  	    );
	  	  }
}
