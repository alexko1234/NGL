package controllers.containers.tpl;

import java.util.ArrayList;
import java.util.List;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;
import views.html.container.*;
import play.i18n.Messages;

public class Containers extends Controller {

	public static Result home(String code){
		return ok(home.render(code));
	}
	
	public static Result backetSearch(){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("code", Messages.get("container.table.code")));
		columns.add(DatatableHelpers.getColumn("categoryCode", Messages.get("container.table.categoryCode")));
		columns.add(DatatableHelpers.getColumn("sampleCodes", Messages.get("container.table.sampleCodes")));
		columns.add(DatatableHelpers.getColumn("fromExperimentTypeCodes", Messages.get("container.table.fromExperimentTypeCodes")));
		columns.add(DatatableHelpers.getColumn("valid", Messages.get("container.table.valid")));
		columns.add(DatatableHelpers.getColumn("support.name", Messages.get("container.table.support.name")));
		columns.add(DatatableHelpers.getColumn("stateCode", Messages.get("container.table.stateCode")));
		DatatableConfig config = new DatatableConfig(columns);
		return ok(basketSearch.render(config));
	}
	
	
	public static Result search(){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("code", Messages.get("containers.table.code")));
		columns.add(DatatableHelpers.getColumn("categoryCode", Messages.get("containers.table.categoryCode")));
		columns.add(DatatableHelpers.getColumn("sampleCodes", Messages.get("containers.table.sampleCodes")));
		columns.add(DatatableHelpers.getColumn("projectCodes", Messages.get("containers.table.projectCodes")));
		columns.add(DatatableHelpers.getColumn("fromExperimentTypeCodes", Messages.get("containers.table.fromExperimentTypeCodes")));
		columns.add(DatatableHelpers.getColumn("valid", Messages.get("containers.table.valid")));
		columns.add(DatatableHelpers.getColumn("support.name", Messages.get("containers.table.support.name")));
		columns.add(DatatableHelpers.getColumn("stateCode", Messages.get("containers.table.stateCode")));
		DatatableConfig config = new DatatableConfig(columns);
		return ok(search.render(config));
	}
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.lists.api.routes.javascript.Lists.projects(),
  	    		controllers.lists.api.routes.javascript.Lists.samples(),
  	    		controllers.containers.api.routes.javascript.Containers.list(),
  	    		controllers.lists.api.routes.javascript.Lists.containerStates(),
  	    		controllers.lists.api.routes.javascript.Lists.containerCategoryCodes()
  	      )	  	      
  	    );
  	}
}
