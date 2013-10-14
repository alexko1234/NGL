package controllers.description.commons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Result;
import services.description.common.LevelService;
import services.description.common.MeasureService;
import controllers.CommonController;

public class Levels extends CommonController {
	
	
	public static Result save(){
		try {
			Map<String,List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();
			LevelService.main(errors);			
			if (errors.size() > 0) {
				return badRequest(Json.toJson(errors));
			} else {
				return ok();
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return internalServerError(e.getMessage());
		}				
	}
}