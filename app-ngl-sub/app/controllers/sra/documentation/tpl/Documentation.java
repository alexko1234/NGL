package controllers.sra.documentation.tpl;

import play.mvc.Controller;
//import play.Routes;
import play.mvc.Result;
import views.html.documentation.home;

import javax.inject.Inject;

// import controllers.CommonController;

// public class Documentation extends -CommonController{

public class Documentation extends Controller {
	
	private home home;
	
	@Inject
	public Documentation(home home) {
		this.home = home;
	}
	
	public Result home() {
		return ok(home.render());
	}
	
}

