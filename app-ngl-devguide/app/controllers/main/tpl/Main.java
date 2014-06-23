package controllers.main.tpl;

import jsmessages.JsMessages;
import play.mvc.Controller;
import play.mvc.Result;

public class Main extends Controller{
	
	  final static JsMessages messages = JsMessages.create(play.Play.application());	
	  
	  public static Result jsMessages() {
	       return ok(messages.generate("Messages")).as("application/javascript");
	   }
}

