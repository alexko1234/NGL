package controllers.sra.documentation.tpl;

import play.Routes;
import play.mvc.Result;
import views.html.documentation.home;
import controllers.CommonController;

public class Documentation extends CommonController{

	public static Result home() {
		return ok(home.render());
	}
	
}
