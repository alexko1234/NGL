package fr.cea.ig.ngl;

import javax.inject.Inject;

import fr.cea.ig.authentication.Authentication;
import fr.cea.ig.lfw.LFWController;
// import fr.cea.ig.ngl.dao.DAOEntityNotFoundException;
// import fr.cea.ig.ngl.daoapi.APIException;
// import play.mvc.Result;
// import fr.cea.ig.ngl.support.APIExecution;
// import fr.cea.ig.ngl.support.LoggerHolder;
// import models.utils.dao.DAOException;
// import play.Logger.ALogger;
// import play.mvc.Result;
// import play.mvc.Results;

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
	
}
