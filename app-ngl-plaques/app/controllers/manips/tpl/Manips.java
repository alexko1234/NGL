package controllers.manips.tpl;

import java.util.ArrayList;
import java.util.List;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;

import views.html.manips.*;

public class Manips extends Controller {

	
	public static Result home(String code) {
		return ok(views.html.manips.home.render());
	}
	
	
	public static Result search() {
		
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("matmaco", "Code"));
		columns.add(DatatableHelpers.getColumn("matmanom", "Nom"));
		DatatableConfig config = new DatatableConfig(columns,Boolean.TRUE, Boolean.FALSE);
		
		return ok(views.html.manips.search.render(config));
	}
	
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.manips.tpl.routes.javascript.Manips.home() ,
  	    		controllers.manips.api.routes.javascript.Manips.list()  
  	      )	  	      
  	    );
  	  }
}
