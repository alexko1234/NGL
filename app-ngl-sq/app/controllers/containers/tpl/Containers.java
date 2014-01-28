package controllers.containers.tpl;

import java.util.ArrayList;
import java.util.List;

import play.Routes;
import play.i18n.Messages;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;
import views.html.container.home;
import views.html.container.search;
import controllers.CommonController;

public class Containers extends CommonController {

	public static Result home(String code){
		return ok(home.render(code));
	}	
	
	public static Result search(){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("support.barCode", Messages.get("containers.table.barCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("support.categoryCode", Messages.get("containers.table.categoryCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("code", Messages.get("containers.table.code"), true, false, false));
		columns.add(DatatableHelpers.getColumn("categoryCode", Messages.get("containers.table.categoryCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("sampleCodes", Messages.get("containers.table.sampleCodes"), true, false, false));
		columns.add(DatatableHelpers.getColumn("projectCodes", Messages.get("containers.table.projectCodes"), true, false, false));
		columns.add(DatatableHelpers.getColumn("fromExperimentTypeCodes", Messages.get("containers.table.fromExperimentTypeCodes"), true, false, false));
		columns.add(DatatableHelpers.getColumn("valid", Messages.get("containers.table.valid"), true, false, false));
		columns.add(DatatableHelpers.getColumn("stateCode", Messages.get("containers.table.stateCode"), true, false, false));
		columns.add(DatatableHelpers.getDateColumn("traceInformation.creationDate", Messages.get("containers.table.creationDate"), true, false, false));
		DatatableConfig config = new DatatableConfig(columns);
		return ok(search.render(config));
	}
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.samples.api.routes.javascript.Samples.list(),
  	    		controllers.containers.api.routes.javascript.Containers.list(),
  	    		controllers.containers.api.routes.javascript.ContainerSupportCategories.list(),
  	    		controllers.containers.api.routes.javascript.Containers.list_supports(),
  	    		controllers.containers.api.routes.javascript.ContainerCategories.list(),
  	    		controllers.commons.api.routes.javascript.States.list(),
  	    		controllers.containers.api.routes.javascript.ContainerCategories.list(),
  	    		controllers.containers.tpl.routes.javascript.Containers.search(),
  	    		controllers.containers.tpl.routes.javascript.Containers.home()
  	      )	  	      
  	    );
  	}
}
