
import play.api.Configuration;
import play.api.Environment;

/**
 * NGL SQ application start module.
 *  
 * @author vrd
 *
 */
public class NGLSQStarterModule extends NGLCommonStarterModule {
	
	/**
	 * Constructor.
	 * @param environment   environment
	 * @param configuration configuration 
	 */
	public NGLSQStarterModule(Environment environment, Configuration configuration) {
		super(environment,configuration);
		logger.debug("created module " + this);
		logger.info("starting NGL-SQ");
		enableDrools();
	}

}

// TODO: remove dead code

/*
public class NGLSQStarterModule extends play.api.inject.Module {
	
	private static final play.Logger.ALogger logger; //  = Logger.of(Module.class);
	
	static {
		logger = play.Logger.of(NGLSQStarterModule.class);
		logger.debug("class " + NGLSQStarterModule.class + " has been loaded, expecting instance creation");
	}

	public NGLSQStarterModule(Environment environment, Configuration configuration) {
		logger.debug("created module " + this);
		logger.info("starting NGL-SQ");
		/ *
		// Set env and config in some global as dependencis on Play.application() are mostly
		// about the configuration.
		fr.cea.ig.play.IGGlobals.environment   = new play.Environment(environment);
		fr.cea.ig.play.IGGlobals.configuration = new play.Configuration(configuration);
		// play.libs.Akka.system(); // This call fails in module/component parts
		// Start a thread to acitvely wait on Play.application() and then start the 
		// Spring "module".
		if (false) {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Play.application();
						try {
							Logger.info("************** SPRING START ************");
							// This would work if the Spring plugin defines a lock to access the spring instance
							// while executing the constructor.
							play.api.modules.spring.SpringPlugin pi = new play.api.modules.spring.SpringPlugin(Play.application().getWrappedApplication());
							Logger.info("************** SPRING DONE  ************");
						} catch (Exception e) {
							e.printStackTrace();
						}
						return;
					} catch (Exception e) {
						// play.application() not set
					}
				}
			}}).start();
		}* /
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
				bind(fr.cea.ig.play.IGGlobals.class                   ).toSelf().eagerly(),
				bind(controllers.resources.AssetPlugin.class          ).toSelf().eagerly(),
				
				// bind(fr.cea.ig.authentication.AuthenticatePlugin.class).toSelf().eagerly(),
				
				// Eager binding allows configuration errors to be detected ASAP
				// bind(fr.cea.ig.authentication.IAuthenticator.class).to(fr.cea.ig.authentication.authenticators.FixedAuthenticator.class).eagerly(),
				// bind(fr.cea.ig.authentication.IAuthenticator.class).to(fr.cea.ig.authentication.authenticators.ErrorAuthenticator.class).eagerly(),
				// bind(fr.cea.ig.authentication.IAuthenticator.class).to(fr.cea.ig.authentication.authenticators.CASAuthenticator.class).eagerly(),
				// bind(fr.cea.ig.authentication.IAuthenticator.class).to(fr.cea.ig.authentication.authenticators.HtmlAuthenticator.class).eagerly(),
				bind(fr.cea.ig.authentication.IAuthenticator.class).to(fr.cea.ig.authentication.authenticators.ConfiguredAuthenticator.class).eagerly(),
				// pass validator is for html login test
				// bind(fr.cea.ig.authentication.ILoginPasswordValidator.class).to(fr.cea.ig.authentication.validators.EqualsLoginPasswordValidator.class).eagerly(),
				
				// bind(fr.cea.ig.authorization.IAuthorizator.class).to(fr.cea.ig.authorization.authorizators.NeverAuthorizator.class),
				bind(fr.cea.ig.authorization.IAuthorizator.class).to(fr.cea.ig.authorization.authorizators.ConfiguredAuthorizator.class),
				 
				// This should possibly be used as the IAuthenticator instance
				// models.administration.authorisation.description.dao.AuthenticateDAO
				
				bind(play.modules.jongo.MongoDBPlugin.class           ).toSelf().eagerly(),
				// was started in the mongodbplugin playplugins. 
				bind(play.modules.mongojack.MongoDBPlugin.class       ).toSelf().eagerly(),
				bind(rules.services.Rules6Component.class             ).toSelf().eagerly(),
				//bind(NGLStarter.class                                 ).toSelf().eagerly() // asEagerSingleton ?
				// Force JsMessages init
				bind(controllers.main.tpl.Main.class                  ).toSelf().eagerly(),
				// The plugins conf stated that it's started last. It should be started after the
				// application is created because of global application instance access but it's not
				// possible anymore. We should be able to use spring as the play injector but the
				// eager initialization of the component-scan part of the configuration fails
				// miserably. We should add @Lazy to @Component.
				bind(play.api.modules.spring.SpringPlugin.class       ).toSelf().eagerly()
			);
	}
	
}
*/

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
/*
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
*/
/*
public class Module extends NGLSQStarterModule {
	public Module(play.api.Environment environment, play.api.Configuration configuration) {
		super(environment,configuration);
	}
}
*/

