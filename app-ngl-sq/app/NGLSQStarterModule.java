// import java.lang.reflect.Method;

// import play.api.Application;
import play.Logger;
//import play.api.Play;
import play.Application;
// import play.api.Application;
//import play.mvc.Action;
//import play.mvc.Http;
//import play.mvc.Http.Request;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
// import play.api.inject.Module;
//import play.GlobalSettings;
// import play.Logger;
// import play.api.Play;
//import play.mvc.Action;
//import play.mvc.Http;
//import play.mvc.Result;
//import play.mvc.Http.Request;
//import rules.services.RulesException;
//import rules.services.RulesServices;
import rules.services.RulesServices6;
import scala.collection.Seq;
//import java.lang.reflect.Method;
import play.inject.ApplicationLifecycle;
import play.libs.F;
//import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;

public class NGLSQStarterModule extends play.api.inject.Module {
	
	private static final Logger.ALogger logger; //  = Logger.of(Module.class);
	
	static {
		logger = Logger.of(NGLSQStarterModule.class);
		logger.debug("class " + NGLSQStarterModule.class + " has been loaded, expecting instance creation");
	}
	
	public NGLSQStarterModule(Environment environment, Configuration configuration) {
		logger.debug("created module " + this);
		logger.info("starting NGL-SQ");
	}
	
	@Override
	public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
		logger.debug("bindings are requested for module " + this);
		
		// -- Recreating the play.conf boot order
		// play.conf :
		//     0:fr.cea.ig.authentication.AuthenticatePlugin
		//     1:controllers.resources.AssetPlugin
		//   200:play.modules.jongo.MongoDBPlugin
		// Added ngl drools startup.
		return seq(
				bind(fr.cea.ig.authentication.AuthenticatePlugin.class).toSelf().eagerly(),
				bind(controllers.resources.AssetPlugin.class          ).toSelf().eagerly(),
				bind(play.modules.jongo.MongoDBPlugin.class           ).toSelf().eagerly(),
				bind(play.modules.mongojack.MongoDBPlugin.class       ).toSelf().eagerly(),
				bind(rules.services.Rules6Component.class             ).toSelf().eagerly()
				//bind(NGLStarter.class                                 ).toSelf().eagerly() // asEagerSingleton ?
			);
	}
	
}

/*
@javax.inject.Singleton
class NGLComponents {
	public static final Logger.ALogger logger = Logger.of(NGLStarter.class);
	@Inject
	public NGLStarter(Application                       application,
			ApplicationLifecycle                        lifecycle,
			fr.cea.ig.authentication.AuthenticatePlugin auth,
			controllers.resources.AssetPlugin           asset,
			play.modules.jongo.MongoDBPlugin            jongo,
			play.modules.mongojack.MongoDBPlugin        mongojack, 
			DroolsComponent                             drools) {
		lifecycle.addStopHook(() -> { 
			onStop(application); 
			return F.Promise.pure(null);
		});
		logger.info("injected NGL started");
	}
	private void onStop(Application application)  {
		logger.info("NGL shutdown...");
	}
}
*/

// Either the object is injected at application start or the instance can be lazily
// created after application start. Instance and injector instance should be the
// same.
@Singleton
class LazyInit {
	
	private static LazyInit instance;
	
	public static LazyInit instance() {
		if (instance == null)
			instance = play.Play.application().injector().instanceOf(LazyInit.class);
		return instance;
	}
	
	@Inject
	public LazyInit(Application app) {
		
	}
	
}

/*
@javax.inject.Singleton
class OnStartComplete {
	private static final Logger.ALogger logger = Logger.of(OnStartComplete.class);
	@Inject
	public OnStartComplete() {
		logger.info("asset server url " + controllers.resources.AssetPlugin.getServer());
	}
}
*/


