package controllers.experiments.tpl;

import java.util.ArrayList;
import java.util.List;

import play.Routes;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;
import views.html.experiments.home;
import views.html.experiments.searchContainers;
import views.html.experiments.newExperiments;

public class Experiments extends Controller{
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
	
	public static Result newExperiments(String experimentTypeCode){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();		
		columns.add(DatatableHelpers.getColumn("code", Messages.get("experiments.table.code")));
		columns.add(DatatableHelpers.getColumn("projectCode", Messages.get("experiments.table.projectCodes")));						
		columns.add(DatatableHelpers.getColumn("sampleCode", Messages.get("experiments.table.sampleCodes")));
			
		DatatableConfig config = new DatatableConfig(columns);
		config.remove = true;
		config.button = true;
		return ok(newExperiments.render(config));
	}
	
	public static Result editExperiment(String experimentTypeCode){
		return ok("test");
	}
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.experiments.tpl.routes.javascript.Experiments.searchContainers(),
  	    		controllers.containers.api.routes.javascript.Containers.list(),
  	    		controllers.lists.api.routes.javascript.Lists.projects(),
  	    		controllers.lists.api.routes.javascript.Lists.samples(),
  	    		controllers.lists.api.routes.javascript.Lists.processTypes(),
  	    		controllers.lists.api.routes.javascript.Lists.experimentTypes(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.newExperiments(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.home(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.editExperiment()
  	      )	  	      
  	    );
  	  }
}
