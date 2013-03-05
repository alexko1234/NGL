package controllers.run;

import play.mvc.Controller;
import play.mvc.Result;
/**
 * @deprecated
 * @author galbini
 *
 */
public class Lanes extends Controller{
	
	
	public static Result createOrUpdate(String code, String format){
		return controllers.run.api.Lanes.save(code);
	}
	
	public static Result show(String code,Integer laneNumber, String format){
		return controllers.run.api.Lanes.get(code, laneNumber);
	}	
}
