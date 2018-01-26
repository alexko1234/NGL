package fr.cea.ig.lfw;

import javax.inject.Inject;

import fr.cea.ig.authentication.Authentication;
import fr.cea.ig.lfw.support.LoggerHolder;
import play.Logger.ALogger;
import play.inject.Injector;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class LFWController extends Controller implements LFWApplicationHolder, LoggerHolder {
	
	private final LFWApplication app;
	
	protected final ALogger logger;

	@Inject
	public LFWController(LFWApplication app) {
		this.app = app;
		logger = play.Logger.of(getClass());
	}
	
	public LFWApplication getLFWApplication() { 
		return app; 
	}
	
	@Override
	public ALogger getLogger() {
		return logger;
	}
	
	public Injector getInjector() {
		return app.injector();
	}
	
	public String getCurrentUser() {
		return Authentication.getUser();
	}
	
	public Result okAsJson(Object o) {
		return ok(Json.toJson(o)).as("application/json");
	}


	// Should implement proper error reporting, does shit atm.
	/*public static Result failure(play.Logger.ALogger logger, String message, Throwable t) {
		logger.error(message,t);
		throw new RuntimeException(t);
	}*/
	
}
