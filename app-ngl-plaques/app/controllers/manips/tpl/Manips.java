package controllers.manips.tpl;

import java.util.ArrayList;
import java.util.List;

import play.Routes;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;

import views.html.manips.*;

public class Manips extends Controller {

	
	public static Result home(String code) {
		return ok(views.html.manips.home.render());
	}
	
	//TODO messages Code,Nom
	public static Result search() {
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("matmaco", "Code"));
		columns.add(DatatableHelpers.getColumn("matmanom", "Nom"));
		DatatableConfig config = new DatatableConfig(columns);
		config.button = Boolean.TRUE;
		return ok(views.html.manips.search.render(config));
	}
	
	//TODO messages Code,Nom
	public static Result list(){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("x", "Position Plaque"));
		columns.add(DatatableHelpers.getColumn("matmaco", "Code"));
		columns.add(DatatableHelpers.getColumn("matmanom", "Nom"));
		DatatableConfig config = new DatatableConfig(columns);
		config.save = Boolean.TRUE;
		config.button=true;
		return ok(views.html.manips.list.render(config));
	}
	
	/*
	public static Result plaque96(){
		return ok(views.html.manips.plaque.render());
	}*/
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.manips.tpl.routes.javascript.Manips.home() ,
  	    		controllers.manips.api.routes.javascript.Manips.list(),
  	  //  		controllers.manips.api.routes.javascript.Manips.plaque(),
  	    		controllers.routes.javascript.Lists.projects(),
  	    		controllers.routes.javascript.Lists.etmateriels(),
  	    		controllers.routes.javascript.Lists.etmanips()
  	      )	  	      
  	    );
  	  }
}
