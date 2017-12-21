package controllers.authorisation.tpl;

import javax.inject.Inject;

import controllers.CommonController;
//import play.Routes;
import play.routing.JavaScriptReverseRouter;
import play.mvc.Result;
import views.html.authorisation.users.*;

/**
 * 
 * @author michieli
 *
 */
public class Users extends CommonController {
	private final home home;
	private final search search;
	@Inject
	public Users(home home, search search) {
		this.home = home;
		this.search= search;
	}
	public Result home(String homecode){
		return ok(home.render(homecode));
	}

	public Result search(){
		return ok(search.render());
	}

	public Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(//Routes.javascriptRouter("jsRoutes",
				JavaScriptReverseRouter.create("jsRoutes",
						//Routes
						controllers.authorisation.tpl.routes.javascript.Users.home(),
						controllers.commons.api.routes.javascript.Users.list(),
						controllers.commons.api.routes.javascript.Roles.list(),
						controllers.commons.api.routes.javascript.Users.update()
						)
				);
	}
}
