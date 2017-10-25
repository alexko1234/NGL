
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
		// Set env and config in some global as dependencis on Play.application() are mostly
		// about the configuration.
		fr.cea.ig.play.IGGlobals.environment   = new play.Environment(environment);
		fr.cea.ig.play.IGGlobals.configuration = new play.Configuration(configuration);
		// play.libs.Akka.system(); // This call fails in module/component parts
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
		// play.plugins still starts from the play-spring-module. The play.plugins
		// file is started after the app injection i believe so the boot order is still
		// correct. Component injection should have proper constructors so the
		// start order has not to be hard coded here.
		return seq(
				bind(play.api.modules.spring.SpringPlugin.class       ).toSelf().eagerly(),
				bind(fr.cea.ig.authentication.AuthenticatePlugin.class).toSelf().eagerly(),
				bind(controllers.resources.AssetPlugin.class          ).toSelf().eagerly(),
				bind(play.modules.jongo.MongoDBPlugin.class           ).toSelf().eagerly(),
				// was started in the mongodbplugin playplugins. 
				bind(play.modules.mongojack.MongoDBPlugin.class       ).toSelf().eagerly(),
				bind(rules.services.Rules6Component.class             ).toSelf().eagerly(),
				//bind(NGLStarter.class                                 ).toSelf().eagerly() // asEagerSingleton ?
				// Force JsMessages init
				bind(controllers.main.tpl.Main.class                  ).toSelf().eagerly()
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
// same. The only required global is the injector that is defined in the play application
// and in the spring plugin. It could be the same in the end but this requires that
// we define an application loader.
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
public class Module extends NGLSQStarterModule {
	public Module(play.api.Environment environment, play.api.Configuration configuration) {
		super(environment,configuration);
	}
}
*/

