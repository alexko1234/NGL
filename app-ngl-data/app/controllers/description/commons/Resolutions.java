package controllers.description.commons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.utils.dao.DAOException;

import play.Logger;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Result;
import services.description.common.ResolutionService;
import controllers.CommonController;
@Deprecated
public class Resolutions extends CommonController {
	
	
	public static Result save(){
		try {
			Map<String,List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();
			ResolutionService.main(errors);
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
