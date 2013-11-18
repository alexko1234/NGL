package controllers.runs.tpl;

import java.util.ArrayList;
import java.util.List;

import controllers.CommonController;
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
public class Runs extends CommonController {
	
	public static Result home(String homecode) {
		return ok(home.render(homecode));
	}
	
	public static Result get(String code) {
		return ok(home.render("search")); 
	}
	
	public static Result validation(String code) {
		return ok(home.render("validation")); 
	}
	
	public static Result search(String type) {
		DatatableConfig config = new DatatableConfig();
		if(!"state".equals(type)){
			
			List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
			columns.add(DatatableHelpers.getColumn("code", Messages.get("runs.table.code"), true, false, false));
			columns.add(DatatableHelpers.getColumn("typeCode", Messages.get("runs.table.typeCode"), true, false, false));
			columns.add(DatatableHelpers.getDateColumn("traceInformation.creationDate", Messages.get("runs.table.creationdate"), true, false, false));
			columns.add(DatatableHelpers.getColumn("state.code", Messages.get("runs.table.stateCode"), true, false, false));
			columns.add(DatatableHelpers.getColumn("validation.valid", Messages.get("runs.table.validation.valid"), true, false, false));
			
			config = new DatatableConfig(columns);	
		}
		return ok(search.render(config));
	}
	
	public static Result detailsDefault() {
		return ok(details.render());
	}
	
	public static Result detailsValidation() {
		return ok(validation.render());
	}
	
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.runs.tpl.routes.javascript.Runs.home(),  
  	    		controllers.runs.tpl.routes.javascript.Runs.get(), 
  	    		controllers.runs.tpl.routes.javascript.Runs.validation(),
  	    		controllers.runs.api.routes.javascript.Runs.get(),
  	    		controllers.readsets.api.routes.javascript.ReadSets.list(),
  	    		controllers.runs.api.routes.javascript.Runs.list(),
  	    		controllers.runs.api.routes.javascript.Runs.state(),
  	    		controllers.runs.api.routes.javascript.Runs.validation(),  	    		
  	    		controllers.runs.api.routes.javascript.Lanes.validation(),  	    		
  	    		controllers.lists.api.routes.javascript.Lists.resolutions()
  	      )	  	      
  	    );
  	  }
	
}
