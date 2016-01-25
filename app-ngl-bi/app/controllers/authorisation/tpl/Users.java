package controllers.authorisation.tpl;

import controllers.CommonController;
import play.Routes;
import play.mvc.Result;
import views.html.authorisation.users.*;

/**
 * 
 * @author michieli
 *
 */
public class Users extends CommonController {
	
	public static Result home(String homecode){
		return ok(home.render(homecode));
	}
	
	public static Result search(){
		return ok(search.render());
	}
	
	public static Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(Routes.javascriptRouter("jsRoutes",
				//Routes
				controllers.authorisation.tpl.routes.javascript.Users.home(),
				controllers.commons.api.routes.javascript.Users.list(),
				controllers.commons.api.routes.javascript.Roles.list(),
				controllers.commons.api.routes.javascript.Users.update()
			)
		);
	}
}
