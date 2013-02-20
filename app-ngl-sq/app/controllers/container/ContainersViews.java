package controllers.container;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.container.*;

public class ContainersViews extends Controller {

	public static Result home(){
		return ok(home.render());
	}
	
	public static Result searchViews(){
		return ok(search.render());
	}
}
