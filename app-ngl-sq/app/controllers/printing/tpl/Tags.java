package controllers.printing.tpl;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.printing.tags.display;


public class Tags extends Controller{
	
	
	
	public static Result display(){
		return ok(display.render());
	}
}
