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
	
	public static Result valuation(String code) {
		return ok(home.render("valuation")); 
	}
	
	public static Result search(String type) {
		
		if(!"valuation".equals(type)){
			return ok(search.render(Boolean.TRUE));
		}else{
			return ok(search.render(Boolean.FALSE));
		}
		
	}
	
	public static Result details() {
		return ok(details.render());
	}
	
	public static Result laneTreatments(String code) {
		return ok(laneTreatments.render(code));
	}
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.runs.tpl.routes.javascript.Runs.home(),  
  	    		controllers.runs.tpl.routes.javascript.Runs.get(), 
  	    		controllers.runs.tpl.routes.javascript.Runs.valuation(),
  	    		controllers.runs.tpl.routes.javascript.Runs.laneTreatments(),
  	    		controllers.runs.api.routes.javascript.Runs.get(),
  	    		controllers.runs.api.routes.javascript.Runs.update(),
  	    		controllers.readsets.api.routes.javascript.ReadSets.list(),
  	    		controllers.readsets.tpl.routes.javascript.ReadSets.get(),
  	    		controllers.readsets.tpl.routes.javascript.ReadSets.home(),
  	    		controllers.runs.api.routes.javascript.Runs.list(),
  	    		controllers.runs.api.routes.javascript.State.update(),
  	    		controllers.runs.api.routes.javascript.State.updateBatch(),
  	    		controllers.runs.api.routes.javascript.Runs.valuation(),  	    		
  	    		controllers.runs.api.routes.javascript.Lanes.valuation(),  	    		
  	    		controllers.valuation.api.routes.javascript.ValuationCriterias.list(),
  	    		controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.samples.api.routes.javascript.Samples.list(),  	    		
  	    		controllers.commons.api.routes.javascript.Resolutions.list(),
  	    		controllers.commons.api.routes.javascript.States.list(),
  	    		controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
  	    		controllers.alerts.api.routes.javascript.Alerts.list(),
  	      		controllers.treatmenttypes.api.routes.javascript.TreatmentTypes.get(),
  	      		controllers.instruments.api.routes.javascript.Instruments.list()
  	      )	  	      
  	    );
  	  }
	
}
