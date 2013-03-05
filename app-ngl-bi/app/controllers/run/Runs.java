package controllers.run;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * Controller around Run object
 * @author galbini
 *
 */
public class Runs extends Controller {
	
	
	
	public static Result show(String code, String format) {
		return controllers.run.api.Runs.get(code);
	}
	
	public static Result createOrUpdate(String format){
		return controllers.run.api.Runs.save();
	}

	public static Result remove(String code,String format){
		return controllers.run.api.Runs.remove(code);
	}
	
	public static Result removeReadsets(String code, String format){
		return controllers.run.api.Runs.removeReadsets(code);
	}
	
	public static Result removeFiles(String code,String format){
		return controllers.run.api.Runs.removeFiles(code);
	}
	
	public static Result dispatch(String code, String format){
		return controllers.run.api.Runs.dispatch(code);
	}
		

}
