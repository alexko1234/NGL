package controllers.reagents.tpl;

import controllers.CommonController;
import play.Routes;
import play.mvc.Result;
import views.html.declarations.home;
import views.html.declarations.kitsCreation;
import views.html.declarations.ordersCreation;
import views.html.declarations.kitsSearch;

public class Orders extends CommonController {
	public Result home(String code){
		return ok(home.render(code+".order"));
	}
	
	public Result search(){
		return ok(kitsSearch.render());
	}
	
	
	public Result get(String code){
		return ok(home.render(code));
	}
	
	public Result createOrEdit(){
		return ok(ordersCreation.render());
	}
}
