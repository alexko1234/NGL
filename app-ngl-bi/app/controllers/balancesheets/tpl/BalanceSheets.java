package controllers.balancesheets.tpl;

import play.Routes;
import play.mvc.Result;
import views.html.balancesheets.*;
import controllers.CommonController;


public class BalanceSheets extends CommonController{
	public static Result home(String homecode){
		return ok(home.render(homecode));
	}
	public static Result year() {
		return ok(year.render());
	}
	public static Result general() {
		return ok(general.render());
	}
	public static Result javascriptRoutes(){
		response().setContentType("text/javascript");
		return ok(Routes.javascriptRouter("jsRoutes",
				controllers.balancesheets.tpl.routes.javascript.BalanceSheets.home(),
				controllers.balancesheets.tpl.routes.javascript.BalanceSheets.year(),
				controllers.balancesheets.tpl.routes.javascript.BalanceSheets.general(),
				controllers.readsets.api.routes.javascript.ReadSets.list(),
				controllers.runs.api.routes.javascript.Runs.list(),
				controllers.projects.api.routes.javascript.Projects.list()
		));
		
	}
}
