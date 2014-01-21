package controllers.main.tpl;

import java.util.List;

import controllers.CommonController;

import jsmessages.JsMessages;

import ls.models.Manip;
import play.api.modules.spring.Spring;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Main extends CommonController {
  
   public static Result index() {
        return ok(index.render());
        
    }
   public static Result jsMessages() {
       return ok(JsMessages.generate("Messages")).as("application/javascript");
   }

}
