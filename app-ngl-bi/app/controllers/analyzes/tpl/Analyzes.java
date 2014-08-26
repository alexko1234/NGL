package controllers.analyzes.tpl;

import play.Routes;
import play.mvc.Result;
import views.html.analyzes.details;
import views.html.analyzes.home;
import views.html.analyzes.search;
import views.html.analyzes.treatments;
import controllers.CommonController;
import controllers.APICommonController;

/**
 * Controller around Run object
 * @author galbini
 *
 */
public class Analyzes extends CommonController {
	
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
  	    		controllers.analyzes.tpl.routes.javascript.Analyzes.home(),  
  	    		controllers.analyzes.tpl.routes.javascript.Analyzes.get(), 
  	    		controllers.analyzes.tpl.routes.javascript.Analyzes.valuation(),
  	    		controllers.analyzes.tpl.routes.javascript.Analyzes.treatments(),
  	    		controllers.analyzes.api.routes.javascript.Analyzes.get(),
  	    		controllers.analyzes.api.routes.javascript.Analyzes.list(),
  	    		controllers.analyzes.api.routes.javascript.Analyzes.list(),
  	    		controllers.analyzes.api.routes.javascript.Analyzes.state(),
  	    		controllers.analyzes.api.routes.javascript.Analyzes.stateBatch(),
  	    		controllers.analyzes.api.routes.javascript.Analyzes.valuation(),
  	    		controllers.analyzes.api.routes.javascript.Analyzes.valuationBatch(),
  	    		controllers.analyzes.api.routes.javascript.Analyzes.properties(),
  	    		controllers.analyzes.api.routes.javascript.Analyzes.propertiesBatch(),
  	    		controllers.samples.api.routes.javascript.Samples.list(),
  	    		controllers.commons.api.routes.javascript.States.list(),
  	    		controllers.commons.api.routes.javascript.StatesHierarchy.list(),
  	    		controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
  	    		controllers.treatmenttypes.api.routes.javascript.TreatmentTypes.get(),
  	      		controllers.treatmenttypes.api.routes.javascript.TreatmentTypes.list(),
  	      		controllers.valuation.api.routes.javascript.ValuationCriterias.list(),
  	      		controllers.valuation.api.routes.javascript.ValuationCriterias.get(),
	    		controllers.projects.api.routes.javascript.Projects.list(),	    		
  	      		controllers.reporting.api.routes.javascript.ReportingConfigurations.list(),
  	      		controllers.reporting.api.routes.javascript.ReportingConfigurations.get(),
  	      		controllers.commons.api.routes.javascript.Users.list(),
  	      		controllers.resolutions.api.routes.javascript.Resolutions.list(),
  	      		controllers.readsets.tpl.routes.javascript.ReadSets.get()
	      		
  	      )	  	      
  	    );
  	  }
	
}
