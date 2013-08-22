package controllers.archives.tpl;

import java.util.ArrayList;
import java.util.List;
import play.Routes;
import play.i18n.Messages;
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
	
	public static Result home(String homecode) {
		return ok(home.render(homecode));
	}
	
	public static Result get(String code) {
		return ok(views.html.archives.home.render("search"));
	}
	
	public static Result search() {
		
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("runCode", Messages.get("archives.table.runcode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("projectCode", Messages.get("archives.table.projectcode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("readSetCode", Messages.get("archives.table.readsetcode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("path", Messages.get("archives.table.path"), true, false, false));
		columns.add(DatatableHelpers.getDateColumn("date", Messages.get("archives.table.date"), true, false, false));
		columns.add(DatatableHelpers.getColumn("id", Messages.get("archives.table.backupid"), true, false, false));
		
		DatatableConfig config = new DatatableConfig(columns);
		return ok(search.render(config));
	}
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    	controllers.archives.tpl.routes.javascript.ReadSets.home(),  
  	    	controllers.archives.tpl.routes.javascript.ReadSets.get(),  
  	    	controllers.archives.api.routes.javascript.ReadSets.list()  	    		
  	      )	  	      
  	    );
  	  }
	
}
