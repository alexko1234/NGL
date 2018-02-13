package controllers.description;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import play.Logger;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
// import services.description.common.InstituteService;
// import services.description.common.LevelService;
import services.description.common.MeasureService;
// import services.description.common.ObjectTypeService;
// import services.description.common.StateService;
import services.description.container.ContainerService;
import services.description.experiment.ExperimentService;
import services.description.instrument.InstrumentService;
import services.description.process.ProcessService;
// import services.description.project.ProjectService;
// import services.description.run.RunService;
// import services.description.run.TreatmentService;
import services.description.sample.ImportService;
import services.description.sample.SampleService;
import fr.cea.ig.ngl.NGLApplication;
import fr.cea.ig.ngl.NGLController;
//import controllers.CommonController;
// import controllers.NGLBaseController;
// import fr.cea.ig.play.NGLContext;

public class NGLSeq extends Controller { // NGLController { // NGLBaseController { //CommonController {
	
//	@Inject
//	public NGLSeq(NGLApplication app) {
//		super(app);
//	}
	
	public Result save() {
		try {
			Map<String,List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();
			//InstituteService.main(errors);
			//ObjectTypeService.main(errors);
			//StateService.main(errors); 
			//ResolutionService.main(errors); 
			//LevelService.main(errors);
			MeasureService.main(errors);
			ContainerService.main(errors);
			InstrumentService.main(errors);
			SampleService.main(errors);
			ImportService.main(errors);
			ExperimentService.main(errors);
			ProcessService.main(errors);
			//ProjectService.main(errors);
			//RunService.main(errors);
			//TreatmentService.main(errors);
			if (errors.size() > 0) {
				return badRequest(Json.toJson(errors));
			} else {
				Logger.info("NGLSeq description is loaded!");
				return ok();
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return internalServerError(e.getMessage());
		}				
	}
	
}
