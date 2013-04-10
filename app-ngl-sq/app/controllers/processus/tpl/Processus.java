package controllers.processus.tpl;

import java.util.ArrayList;
import java.util.List;

import play.Routes;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;

import views.html.processus.*;

public class Processus extends Controller{
	
	public static Result home(String code){
		return ok(home.render());
	}
	
	public static Result search(){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("code", Messages.get("container.table.code")));
		columns.add(DatatableHelpers.getColumn("categoryCode", Messages.get("container.table.categoryCode")));
		columns.add(DatatableHelpers.getColumn("sampleCode", Messages.get("container.table.sampleCode")));
		columns.add(DatatableHelpers.getColumn("fromExperimentTypeCodes", Messages.get("container.table.fromExperimentTypeCodes")));
		columns.add(DatatableHelpers.getColumn("valid", Messages.get("container.table.valid")));
		columns.add(DatatableHelpers.getColumn("support.name", Messages.get("container.table.support.name")));
		columns.add(DatatableHelpers.getColumn("stateCode", Messages.get("container.table.stateCode")));
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
  	    		controllers.processus.tpl.routes.javascript.Processus.home(),  
  	    		controllers.tpl.helper.routes.javascript.Lists.projects(),
  	    		controllers.tpl.helper.routes.javascript.Lists.samples(),
  	    		controllers.tpl.helper.routes.javascript.Lists.experimentTypes(),
  	    		controllers.tpl.helper.routes.javascript.Lists.processusType()
  	      )	  	      
  	    );
  	  }
}
