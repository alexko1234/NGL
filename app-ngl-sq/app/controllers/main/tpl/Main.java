package controllers.main.tpl;

import jsmessages.JsMessages;
import play.Routes;
import play.mvc.Result;
import views.html.home;
import controllers.CommonController;
import controllers.authorisation.Authenticate;
import controllers.authorisation.Permission;


public class Main extends CommonController{

   public static Result home() {
        return ok(home.render());
   }
   
   public static Result javascriptRoutes() {
 	    response().setContentType("text/javascript");
 	    return ok(	  	      
 	        Routes.javascriptRouter("jsRoutes"  	       
 	        // Routes	  	         	        
 	      )
 	    );
 	  }
   
   
   public static Result jsMessages() {
       return ok(JsMessages.generate("Messages")).as("application/javascript");
   }

}
