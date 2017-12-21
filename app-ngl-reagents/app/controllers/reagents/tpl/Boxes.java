package controllers.reagents.tpl;

import play.mvc.Result;
import views.html.declarations.home;
import views.html.declarations.boxesSearch;

import javax.inject.Inject;

import controllers.CommonController;

public class Boxes  extends CommonController {
	
	private final home home;
	private final boxesSearch boxesSearch;
	@Inject
	public Boxes(home home, boxesSearch boxesSearch) {
		this.home = home;
		this.boxesSearch = boxesSearch;
	}
	
	public Result home(String code){
		return ok(home.render(code+".boxes"));
	}
	
	public Result search(){
		return ok(boxesSearch.render());
	}
	
}
