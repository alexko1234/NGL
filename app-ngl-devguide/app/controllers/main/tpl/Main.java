package controllers.main.tpl;

import jsmessages.JsMessages;
import play.mvc.Controller;
import play.mvc.Result;

public class Main extends Controller{
	  public static Result jsMessages() {
	       return ok(JsMessages.generate("Messages")).as("application/javascript");
	   }
}

