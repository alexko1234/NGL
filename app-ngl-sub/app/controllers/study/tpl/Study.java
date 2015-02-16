package controllers.study.tpl;

import controllers.CommonController;
import play.mvc.Result;
import views.html.study.home;
import views.html.study.create;

public class Study extends CommonController
{
	
	public static Result home(String homecode) {
		return ok(home.render(homecode));
	}

	public static Result create() {
		return ok(create.render());
	}
}
