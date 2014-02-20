
import java.lang.reflect.Method;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Http.Request;
import fr.cea.ig.authentication.Authenticate;

public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		Logger.info("NGL has started");
	}
	
	public Action onRequest(Request request, Method actionMethod) {
		return super.onRequest(request, actionMethod);
	}

	@Override
	public  play.api.mvc.Handler onRouteRequest(Http.RequestHeader request) {
		return super.onRouteRequest(request);
	}

	@Override
	public void onStop(Application app) {
		Logger.info("NGL shutdown...");
	}


}