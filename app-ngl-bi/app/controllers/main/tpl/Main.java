package controllers.main.tpl;

import controllers.CommonController;
import jsmessages.JsMessages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.home ;


public class Main extends CommonController {


   public static Result home() {
	   return ok(home.render());
        
    }
   
   public static Result jsMessages() {
       return ok(JsMessages.generate("Messages")).as("application/javascript");
   }
   
   

}
