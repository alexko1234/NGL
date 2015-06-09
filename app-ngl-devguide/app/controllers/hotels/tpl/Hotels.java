package controllers.hotels.tpl;

import controllers.CommonController;

import play.Routes;
import play.mvc.Result;

public class Hotels extends CommonController {
	
	public static Result home(String homecode){		
		return ok(views.html.hotels.home.render(homecode));
	}
		
	public static Result get(String code){
		return ok(views.html.hotels.home.render("search"));
	}
	
	public static Result search(){		
		return ok(views.html.hotels.search.render());
	}
		
	public static Result details(){
		return ok(views.html.hotels.details.render());
	}
	
	public static Result javascriptRoutes() {
	    response().setContentType("text/javascript");
	  	    return ok(  	    		
	  	      Routes.javascriptRouter("jsRoutes",
	  	        // Routes
	  	    		controllers.hotels.tpl.routes.javascript.Hotels.home(),
	  	    		controllers.hotels.tpl.routes.javascript.Hotels.details(),
	  	    		controllers.hotels.api.routes.javascript.Hotels.get(),
	  	    		controllers.hotels.api.routes.javascript.Hotels.list(),
	  	    		controllers.hotels.api.routes.javascript.Bedrooms.get(),
	  	    		controllers.hotels.api.routes.javascript.Bedrooms.list(),
	  	    		controllers.hotels.api.routes.javascript.Bedrooms.save(),
	  	    		controllers.hotels.api.routes.javascript.Bedrooms.delete()
	  	      )	  	      
		    );
	}
}
