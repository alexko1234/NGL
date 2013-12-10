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
  	    		controllers.readsets.api.routes.javascript.ReadSets.valuation(),  	
  	    		controllers.runs.api.routes.javascript.Runs.get(),
  	    		controllers.runs.tpl.routes.javascript.Runs.get(),
  	    		controllers.lists.api.routes.javascript.Lists.resolutions(),
  	    		controllers.lists.api.routes.javascript.Lists.valuationCodes(),
  	    		controllers.lists.api.routes.javascript.Lists.projects(),
  	    		controllers.lists.api.routes.javascript.Lists.samples(),
  	    		controllers.commons.api.routes.javascript.States.list(),
  	    		controllers.commons.api.routes.javascript.CommonInfoTypes.list()
  	      )	  	      
  	    );
  	  }
	
}
