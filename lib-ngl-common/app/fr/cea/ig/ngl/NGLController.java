package fr.cea.ig.ngl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import fr.cea.ig.lfw.LFWController;
import play.data.Form;
import play.data.validation.ValidationError;

public class NGLController extends LFWController implements NGLApplicationHolder {

	private final NGLApplication app;
	
	@Inject
	public NGLController(NGLApplication app) {
		super(app);
		this.app = app;
	}
	
	@Override
	public NGLApplication getNGLApplication() {
		return app; 
	}
	
	public NGLConfig getConfig() {
		return app.nglConfig();
	}
	
	/*public Result result(APIExecution toRun, String msg) {
		try {
			return toRun.run();
		} catch (DAOEntityNotFoundException e) {
			return Results.notFound();
		} catch (Exception e) {
			return failure(logger,msg,e);
		}
	}*/
	
	// -- form deprecation work around
	public static Map<String,List<ValidationError>> getFormErrors(Form<?> form) {
		// TODO : find a proper way to pass errors around
		@SuppressWarnings("deprecation")
		Map<String,List<ValidationError>> errors = form.errors();
		return errors;
	}
	
}
