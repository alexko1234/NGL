package controllers.processes.tpl;

import java.util.ArrayList;
import java.util.List;

import controllers.processes.tpl.routes.javascript;

import play.Routes;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;

import views.html.processes.*;

public class Processes extends Controller{
	
	public static Result home(String code){
		return ok(home.render());
	}
	
	public static Result search(){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("code", Messages.get("container.table.code"), true, false, false));
		columns.add(DatatableHelpers.getColumn("projectCodes", Messages.get("container.table.projectCodes"), true, false, false));				
		columns.add(DatatableHelpers.getColumn("sampleCodes", Messages.get("container.table.sampleCodes"), true, false, false));		
		columns.add(DatatableHelpers.getColumn("valid", Messages.get("container.table.valid"), true, false, false));
		columns.add(DatatableHelpers.getColumn("stateCode", Messages.get("container.table.stateCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("categoryCode", Messages.get("container.table.categoryCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("fromExperimentTypeCodes", Messages.get("container.table.fromExperimentTypeCodes")));
		
		DatatableConfig config = new DatatableConfig(columns);
		config.button = Boolean.TRUE;
		return ok(search.render(config));
	}
	
	public static Result list(){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("code", Messages.get("container.table.code")));
		columns.add(DatatableHelpers.getColumn("categoryCode", Messages.get("container.table.categoryCode")));
		columns.add(DatatableHelpers.getColumn("sampleCode", Messages.get("container.table.sampleCode")));
		columns.add(DatatableHelpers.getColumn("fromExperimentTypeCodes", Messages.get("container.table.fromExperimentTypeCodes")));
		columns.add(DatatableHelpers.getColumn("valid", Messages.get("container.table.valid")));
		columns.add(DatatableHelpers.getColumn("support.name", Messages.get("container.table.support.name")));
		columns.add(DatatableHelpers.getColumn("stateCode", Messages.get("container.table.stateCode")));
		DatatableConfig config = new DatatableConfig(columns,true, true);
		config.button = Boolean.TRUE;
		return ok(list.render(config));
	}
	
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.processes.tpl.routes.javascript.Processes.home(),  
  	    		controllers.lists.api.routes.javascript.Lists.projects(),
  	    		controllers.lists.api.routes.javascript.Lists.samples(),
  	    		controllers.lists.api.routes.javascript.Lists.processTypes()
  	      )	  	      
  	    );
  	  }
}
