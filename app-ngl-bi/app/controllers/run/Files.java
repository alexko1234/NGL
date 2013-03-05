package controllers.run;

import play.mvc.Controller;
import play.mvc.Result;
/**
 * @deprecated
 * @author galbini
 *
 */
public class Files extends Controller{
	
	
	public static Result createOrUpdate(String readsetCode, String format){
		return controllers.run.api.Files.save(readsetCode);
	}
	
	
	public static Result show(String readsetCode,String fullname, String format){
		return controllers.run.api.Files.get(readsetCode, fullname);
	}
	
}
