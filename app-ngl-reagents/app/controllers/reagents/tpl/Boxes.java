package controllers.reagents.tpl;

import play.mvc.Result;
import views.html.declarations.home;
import views.html.declarations.boxesSearch;
import controllers.CommonController;

public class Boxes  extends CommonController {
	public Result home(String code){
		return ok(home.render(code+".boxes"));
	}
	
	public Result search(){
		return ok(boxesSearch.render());
	}
	
}
