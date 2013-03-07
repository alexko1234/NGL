package controllers.archives.tpl;

import java.util.ArrayList;
import java.util.List;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;
import views.html.archives.*;
/**
 * Controller around archive readset object
 * @author galbini
 *
 */
public class ReadSets extends Controller {
	
	public static Result home() {
		return ok(home.render());
	}
	
	public static Result search() {
		
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("runCode", "Run Code"));
		columns.add(DatatableHelpers.getColumn("projectCode", "Project Code"));
		columns.add(DatatableHelpers.getColumn("readSetCode", "Read Set Code"));
		columns.add(DatatableHelpers.getColumn("path", "Path"));
		columns.add(DatatableHelpers.getDateColumn("date", "Date"));
		columns.add(DatatableHelpers.getColumn("id", "Id"));
		DatatableConfig config = new DatatableConfig(columns);
		return ok(search.render(config));
	}
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.archives.tpl.routes.javascript.ReadSets.home(),  
  	    		controllers.archives.api.routes.javascript.ReadSets.list()  	    		
  	      )	  	      
  	    );
  	  }
	
}
