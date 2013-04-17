package controllers.runs.tpl;

import java.util.ArrayList;
import java.util.List;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;
import views.html.runs.*;

/**
 * Controller around Run object
 * @author galbini
 *
 */
public class Runs extends Controller {
	
	public static Result home(String code) {
		return ok(home.render());
	}
	
	public static Result search() {
		
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("code", "Code", true, false, false));
		columns.add(DatatableHelpers.getDateColumn("traceInformation.creationDate", "Creation date", true, false, false));
		columns.add(DatatableHelpers.getColumn("dispatch", "Dispatch", true, false, false));
		DatatableConfig config = new DatatableConfig(columns);
		config.show = true;
		return ok(search.render(config));
	}
	
	public static Result details() {
		return ok(details.render());
	}
	
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.runs.tpl.routes.javascript.Runs.home(),  
  	    		controllers.runs.api.routes.javascript.Runs.get(),
  	    		controllers.runs.api.routes.javascript.Runs.list()  	    		
  	      )	  	      
  	    );
  	  }
	
}
