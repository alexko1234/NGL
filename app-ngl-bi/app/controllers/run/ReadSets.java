package controllers.run;

import play.mvc.Controller;
import play.mvc.Result;
/**
 * @deprecated
 * @author galbini
 *
 */
public class ReadSets extends Controller{

	
	public static Result createOrUpdate(String code, Integer laneNumber, String format){		
		return controllers.run.api.ReadSets.save(code, laneNumber);
	}
	
	public static Result update(String readSetCode, String format){
		return controllers.run.api.ReadSets.update(readSetCode);
	}
	
	public static Result show(String code,Integer laneNumber,String readSetCode,String format){
		return controllers.run.api.ReadSets.get(readSetCode);
	}
	
	public static Result showWithReadsetCode(String readSetCode,String format){
		return controllers.run.api.ReadSets.get(readSetCode);
	}
	
	public static Result needArchive(String format){
		return controllers.archive.api.ReadSets.list(2);
	}
	
	public static Result updateArchive(String readSetCode,String format){
		return controllers.archive.api.ReadSets.update(readSetCode);
	}
	
}
