package controllers.balancesheets.tpl;

// import play.Routes;
import play.routing.JavaScriptReverseRouter;
import play.mvc.Result;
import views.html.balancesheets.*;

import java.util.Calendar;

import javax.inject.Inject;

import controllers.CommonController;


public class BalanceSheets extends CommonController {
	
	private final home home;
	private final year year;
	private final general general;
	@Inject
	public BalanceSheets(home home, year year, general general) {
		this.home = home;
		this.year = year;
		this.general = general;
	}
	public Result home(String homecode, String year) {
		return ok(home.render(homecode, year));
	}
	public Result year() {
		return ok(year.render());
	}
	public Result general() {
		return ok(general.render());
	}
	public Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(// Routes.javascriptRouter("jsRoutes",
				JavaScriptReverseRouter.create("jsRoutes",
				controllers.balancesheets.tpl.routes.javascript.BalanceSheets.home(),
				controllers.balancesheets.tpl.routes.javascript.BalanceSheets.year(),
				controllers.balancesheets.tpl.routes.javascript.BalanceSheets.general(),
				controllers.readsets.api.routes.javascript.ReadSets.list(),
				controllers.runs.api.routes.javascript.Runs.list(),
				controllers.projects.api.routes.javascript.Projects.list(),
				controllers.commons.api.routes.javascript.CommonInfoTypes.list()
		));
		
	}
}
