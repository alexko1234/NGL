package controllers.main.tpl;

import controllers.CommonController;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import jsmessages.JsMessages;
import views.html.home ;


public class Main extends CommonController {

   final static JsMessages messages = JsMessages.create(play.Play.application());	
	
   public static Result home() {
	   return ok(home.render());
        
    }
   
   public static Result jsMessages() {
       return ok(messages.generate("Messages")).as("application/javascript");

   }
 
}
