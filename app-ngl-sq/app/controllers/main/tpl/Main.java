package controllers.main.tpl;

import jsmessages.JsMessages;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Main extends Controller {
  
   public static Result index() {
        return ok(index.render());
        
   }
   
   public static Result javascriptRoutes() {
 	    response().setContentType("text/javascript");
 	    return ok(	  	      
 	        Routes.javascriptRouter("jsRoutes",	  	       
 	        // Routes	  	       
 	        controllers.administration.authentication.routes.javascript.User.logOut()	  	        
 	      )
 	    );
 	  }
   
   
   public static Result jsMessages() {
       return ok(JsMessages.generate("Messages")).as("application/javascript");
   }

}
