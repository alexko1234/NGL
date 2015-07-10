package controllers.bookings.tpl;

import controllers.CommonController;

import play.Routes;
import play.mvc.Result;

public class Bookings extends CommonController {
	
	public static Result home(String homecode){		
		return ok(views.html.bookings.home.render(homecode));
	}
		
	public static Result get(String code){
		return ok(views.html.bookings.home.render("search"));
	}
	
	public static Result search(){		
		return ok(views.html.bookings.search.render());
	}
		
	public static Result details(){
		return ok();
	}
	
	public static Result javascriptRoutes() {
	    response().setContentType("text/javascript");
	  	    return ok(  	    		
	  	      Routes.javascriptRouter("jsRoutes",
	  	        // Routes
	  	    		controllers.bookings.tpl.routes.javascript.Bookings.home(),
	  	    		controllers.bookings.tpl.routes.javascript.Bookings.details(),
	  	    		controllers.bookings.api.routes.javascript.Bookings.get(),
	  	    		controllers.bookings.api.routes.javascript.Bookings.list(),
	  	    		controllers.bookings.api.routes.javascript.Bookings.get(),
	  	    		controllers.bookings.api.routes.javascript.Bookings.save(),
	  	    		controllers.bookings.api.routes.javascript.Bookings.delete()
	  	      )	  	      
		    );
	}
}
