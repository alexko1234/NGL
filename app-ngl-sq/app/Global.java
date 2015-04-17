
import java.lang.reflect.Method;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.api.Play;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Http.Request;
import rules.services.RulesException;
import rules.services.RulesServices;
import rules.services.RulesServices6;

public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		Logger.error("NGL-SQ has started");
		
		Logger.info("Load knowledge base");
		
		try {
			RulesServices6.getInstance();
		} catch (Throwable e) {
			Logger.error("Error Load knowledge base");
			e.printStackTrace();
			//Shutdown application
			Play.stop();
		}
		
		/*
		RulesServices rulesServices = new RulesServices();
		try {
			rulesServices.buildKnowledgeBase();
			
		} catch (RulesException e) {
			Logger.error("Error Load knowledge base");
			e.printStackTrace();
			//Shutdown application
			Play.stop();
		}
		*/
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