package controllers.runs.tpl;

import java.util.ArrayList;
import java.util.List;

import jsmessages.JsMessages;

import play.Routes;
import play.i18n.Messages;
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
	
	public static Result home(String homecode) {
		return ok(home.render(homecode));
	}
	
	public static Result get(String code) {
		return ok(views.html.runs.home.render("search"));
	}
	
	public static Result search() {
		
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("code", Messages.get("runs.table.code"), true, false, false));
		columns.add(DatatableHelpers.getDateColumn("traceInformation.creationDate", Messages.get("runs.table.creationdate"), true, false, false));
		columns.add(DatatableHelpers.getColumn("dispatch", Messages.get("runs.table.dispatch"), true, false, false));
		DatatableConfig config = new DatatableConfig(columns);
		config.show = true;
		config.button = true;
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
  	    		controllers.runs.tpl.routes.javascript.Runs.get(),  
  	    		controllers.runs.api.routes.javascript.Runs.get(),
  	    		controllers.runs.api.routes.javascript.Runs.list()  	    		
  	      )	  	      
  	    );
  	  }
	
}
