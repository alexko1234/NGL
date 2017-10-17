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
import javax.inject.Inject;
import play.inject.ApplicationLifecycle;
import play.libs.F;
//import play.inject.ApplicationLifecycle;


// Rename to NGLStartModule
public class Module extends play.api.inject.Module {
	
	private static final Logger.ALogger logger; //  = Logger.of(Module.class);
	
	static {
		logger = Logger.of(Module.class);
		logger.debug("class " + Module.class + " has been loaded, expecting instance creation");
	}
	
	public Module(Environment environment, Configuration configuration) {
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
				// bind(fr.cea.ig.authentication.AuthenticatePlugin.class).toSelf().eagerly(),
				// bind(controllers.resources.AssetPlugin.class          ).toSelf().eagerly(),
				// bind(play.modules.jongo.MongoDBPlugin.class           ).toSelf().eagerly(),
				// bind(DroolsComponent.class                            ).toSelf().eagerly(),
				// bind(OnStartComplete.class                            ).toSelf().eagerly()
				bind(NGLStarter.class                                 ).toSelf().eagerly() // asEagerSingleton ?
			);
	}
	
}

@javax.inject.Singleton
class NGLStarter {
	public static final Logger.ALogger logger = Logger.of(NGLStarter.class);
	@Inject
	public NGLStarter(Application application,
			ApplicationLifecycle lifecycle,
			fr.cea.ig.authentication.AuthenticatePlugin auth,
			controllers.resources.AssetPlugin           asset,
			play.modules.jongo.MongoDBPlugin            jongo,
			play.modules.mongojack.MongoDBPlugin        mongojack, 
			DroolsComponent                             drools) {
		logger.info("injected NGL started");
	}
}

@javax.inject.Singleton
class OnStartComplete {
	private static final Logger.ALogger logger = Logger.of(OnStartComplete.class);
	@Inject
	public OnStartComplete() {
		logger.info("asset server url " + controllers.resources.AssetPlugin.getServer());
	}
}

@javax.inject.Singleton
class DroolsComponent {
	
	private static final Logger.ALogger logger = Logger.of(DroolsComponent.class);
	
	@Inject
	public DroolsComponent(Application                                    app, 
							ApplicationLifecycle                          lifecycle) {
		logger.debug("injecting " + app);
		onStart(app,lifecycle);
		logger.debug("injected");
	}
	
	public void onStart(Application app, ApplicationLifecycle lifecycle) {
		logger.info("loading knowledge base");
		try {
			// RulesServices6.getInstance();
			RulesServices6.initSingleton(app);
			// Pretty much pointless, the message "NGL shutdown should not be displayed there
			lifecycle.addStopHook(() -> { 
						onStop(app); 
						return F.Promise.pure(null);
					});
			logger.info("drools started");
		} catch (Throwable e) {
			logger.error("error loading drools knowledge base " + e.getMessage(),e);
			//Shutdown application
			//Play.stop(app.getWrappedApplication());
			logger.info("shutting down app after drools initialization error");
		}
	}
	
	public void onStop(Application app) {
		logger.info("NGL shutdown...");
	}
	
}

