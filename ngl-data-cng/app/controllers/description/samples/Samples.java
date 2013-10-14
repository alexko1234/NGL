package controllers.description.samples;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Result;
import services.description.experiment.ExperimentService;
import services.description.instrument.InstrumentService;
import services.description.project.ProjectService;
import services.description.sample.ImportService;
import services.description.sample.SampleService;

import controllers.CommonController;

public class Samples extends CommonController {
	public static Result save(){
		try {
			Map<String,List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();
			SampleService.main(errors);
			ImportService.main(errors);
			
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
