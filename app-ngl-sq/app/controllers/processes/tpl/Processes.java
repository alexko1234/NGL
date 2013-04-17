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
		return ok(home.render(code));
	}
	
	public static Result searchContainers(){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("code", Messages.get("containers.table.code"), true, false, false));
		columns.add(DatatableHelpers.getColumn("projectCodes", Messages.get("containers.table.projectCodes"), true, false, false));				
		columns.add(DatatableHelpers.getColumn("sampleCodes", Messages.get("containers.table.sampleCodes"), true, false, false));		
		columns.add(DatatableHelpers.getColumn("valid", Messages.get("containers.table.valid"), true, false, false));
		columns.add(DatatableHelpers.getColumn("stateCode", Messages.get("containers.table.stateCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("categoryCode", Messages.get("containers.table.categoryCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("fromExperimentTypeCodes", Messages.get("containers.table.fromExperimentTypeCodes")));
		
		DatatableConfig config = new DatatableConfig(columns);
		config.button = Boolean.TRUE;
		return ok(searchContainers.render(config));
	}
	
	public static Result newProcesses(String processTypeCode){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();		
		columns.add(DatatableHelpers.getColumn("code", Messages.get("processes.table.code")));
		columns.add(DatatableHelpers.getColumn("projectCode", Messages.get("processes.table.projectCode")));						
		columns.add(DatatableHelpers.getColumn("sampleCode", Messages.get("processes.table.sampleCode")));
		columns.add(DatatableHelpers.getColumn("containerInputCode", Messages.get("processes.table.containerInputCode")));
		columns.add(DatatableHelpers.getColumn("stateCode", Messages.get("processes.table.stateCode")));
		DatatableConfig config = new DatatableConfig(columns);
		config.save = true;
		config.edit = true;
		config.remove = true;
		config.button = true;
		return ok(newProcesses.render(config));
	}
	
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.processes.tpl.routes.javascript.Processes.newProcesses(),  
  	    		controllers.processes.tpl.routes.javascript.Processes.home(),  
  	    		controllers.lists.api.routes.javascript.Lists.projects(),
  	    		controllers.lists.api.routes.javascript.Lists.samples(),
  	    		controllers.lists.api.routes.javascript.Lists.processTypes()
  	      )	  	      
  	    );
  	  }
}
