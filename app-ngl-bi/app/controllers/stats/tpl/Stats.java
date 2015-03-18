package controllers.stats.tpl;

import play.Routes;
import play.mvc.Result;
import views.html.stats.*;
import controllers.CommonController;

/**
 * Controller around Run object
 * @author galbini
 *
 */
public class Stats extends CommonController {
	
	public static Result home(String homecode) {
		return ok(home.render(homecode));
	}
	
	public static Result config(String type) {
		return ok(config.render());
	}
	
	public static Result show(String type) {
		return ok(show.render());
	}
	
	public static Result choice(String type) {
		return ok(choice.render());
	}
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.stats.tpl.routes.javascript.Stats.home(),  
  	    		controllers.stats.tpl.routes.javascript.Stats.config(),
  	    		controllers.stats.api.routes.javascript.StatsConfigurations.list(),
  	    		controllers.stats.api.routes.javascript.StatsConfigurations.get(),
  	    		controllers.readsets.tpl.routes.javascript.ReadSets.home(),  
  	    		controllers.readsets.tpl.routes.javascript.ReadSets.get(),   	    		
  	    		controllers.readsets.tpl.routes.javascript.ReadSets.detailsPrintView(),
  	    		controllers.readsets.tpl.routes.javascript.ReadSets.other(), 
  	    		controllers.readsets.tpl.routes.javascript.ReadSets.valuation(),
  	    		controllers.readsets.tpl.routes.javascript.ReadSets.treatments(),
  	    		controllers.readsets.api.routes.javascript.ReadSets.get(),
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
  	    		controllers.commons.api.routes.javascript.StatesHierarchy.list(),
  	    		controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
  	      		controllers.treatmenttypes.api.routes.javascript.TreatmentTypes.get(),
  	      		controllers.treatmenttypes.api.routes.javascript.TreatmentTypes.list(),
  	      		controllers.instruments.api.routes.javascript.Instruments.list(),
  	      		controllers.valuation.api.routes.javascript.ValuationCriterias.list(),
  	      		controllers.valuation.api.routes.javascript.ValuationCriterias.get(),
	    		controllers.projects.api.routes.javascript.Projects.list(),	    		
  	      		controllers.reporting.api.routes.javascript.ReportingConfigurations.list(),
  	      		controllers.reporting.api.routes.javascript.ReportingConfigurations.get(),
  	      		controllers.reporting.api.routes.javascript.FilteringConfigurations.list(),
  	      		controllers.commons.api.routes.javascript.Users.list(),
  	      		controllers.resolutions.api.routes.javascript.Resolutions.list(),
  	      		controllers.projects.api.routes.javascript.Projects.get(),
  	      		controllers.commons.api.routes.javascript.Values.list()
  	      )	  	      
  	    );
  	  }
	
}
