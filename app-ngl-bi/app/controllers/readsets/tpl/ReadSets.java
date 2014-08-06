package controllers.readsets.tpl;

import play.Routes;
import play.mvc.Result;
import views.html.readsets.details;
import views.html.readsets.home;
import views.html.readsets.search;
import views.html.readsets.treatments;
import controllers.CommonController;

/**
 * Controller around Run object
 * @author galbini
 *
 */
public class ReadSets extends CommonController {
	
	public static Result home(String homecode) {
		return ok(home.render(homecode));
	}
	
	public static Result get(String code) {
		return ok(home.render("search")); 
	}
	
	public static Result valuation(String code) {
		return ok(home.render("valuation")); 
	}
	
	public static Result search() {
		return ok(search.render());		
	}
	
	public static Result details() {
		return ok(details.render());
	}
	
	public static Result treatments(String code) {
		return ok(treatments.render(code));
	}
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.readsets.tpl.routes.javascript.ReadSets.home(),  
  	    		controllers.readsets.tpl.routes.javascript.ReadSets.get(), 
  	    		controllers.readsets.tpl.routes.javascript.ReadSets.valuation(),
  	    		controllers.readsets.tpl.routes.javascript.ReadSets.treatments(),
  	    		controllers.readsets.api.routes.javascript.ReadSets.get(),
  	    		controllers.readsets.api.routes.javascript.ReadSets.list(),
  	    		controllers.readsets.api.routes.javascript.ReadSets.list(),
  	    		controllers.readsets.api.routes.javascript.ReadSets.state(),
  	    		controllers.readsets.api.routes.javascript.ReadSets.stateBatch(),
  	    		controllers.readsets.api.routes.javascript.ReadSets.valuation(),
  	    		controllers.readsets.api.routes.javascript.ReadSets.valuationBatch(),
  	    		controllers.readsets.api.routes.javascript.ReadSets.properties(),
  	    		controllers.readsets.api.routes.javascript.ReadSets.propertiesBatch(),
  	    		controllers.runs.api.routes.javascript.Lanes.get(),
  	    		controllers.runs.api.routes.javascript.Runs.get(),
  	    		controllers.runs.api.routes.javascript.Runs.list(),
  	    		controllers.runs.api.routes.javascript.RunTreatments.get(),
  	    		controllers.runs.tpl.routes.javascript.Runs.get(),
  	    		controllers.samples.api.routes.javascript.Samples.list(),
  	    		controllers.commons.api.routes.javascript.States.list(),
  	    		controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
  	      		controllers.treatmenttypes.api.routes.javascript.TreatmentTypes.get(),
  	      		controllers.treatmenttypes.api.routes.javascript.TreatmentTypes.list(),
  	      		controllers.instruments.api.routes.javascript.Instruments.list(),
  	      		controllers.valuation.api.routes.javascript.ValuationCriterias.list(),
  	      		controllers.valuation.api.routes.javascript.ValuationCriterias.get(),
	    		controllers.projects.api.routes.javascript.Projects.list(),	    		
  	      		controllers.reporting.api.routes.javascript.ReportingConfigurations.list(),
  	      		controllers.reporting.api.routes.javascript.ReportingConfigurations.get(),
  	      		controllers.commons.api.routes.javascript.Users.list(),
  	      		controllers.resolutions.api.routes.javascript.Resolutions.list(),
  	      		controllers.projects.api.routes.javascript.Projects.get()
  	      )	  	      
  	    );
  	  }
	
}
