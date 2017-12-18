


import play.Logger;
import play.Application;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
//import rules.services.RulesServices6;
import scala.collection.Seq;
import play.inject.ApplicationLifecycle;
import play.libs.F;

import javax.inject.Inject;
import javax.inject.Singleton;

public class NGLSubStarterModule extends play.api.inject.Module {
	
	private static final Logger.ALogger logger = Logger.of(NGLSubStarterModule.class);

	public NGLSubStarterModule(Environment environment, Configuration configuration) {
		logger.debug("created module " + this);
		logger.info("starting NGL-Sub");
	}

	// 0:fr.cea.ig.authentication.AuthenticatePlugin
	// 1:controllers.resources.AssetPlugin
	@Override
	public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
		logger.debug("bindings are requested for module " + this);
		return seq(
				bind(fr.cea.ig.play.IGGlobals.class                   ).toSelf().eagerly(),
				
				//bind(fr.cea.ig.authentication.AuthenticatePlugin.class).toSelf().eagerly(),
				bind(fr.cea.ig.authentication.IAuthenticator.class)
				  .to(fr.cea.ig.authentication.authenticators.ConfiguredAuthenticator.class).eagerly(),
				
				bind(controllers.resources.AssetPlugin.class          ).toSelf().eagerly(),
				// bind(play.modules.jongo.MongoDBPlugin.class           ).toSelf().eagerly(),
				bind(play.modules.mongojack.MongoDBPlugin.class       ).toSelf().eagerly(),
				// Force JsMessages init
				bind(controllers.main.tpl.Main.class                  ).toSelf().eagerly(),
				bind(play.api.modules.spring.SpringPlugin.class       ).toSelf().eagerly()
				);
	}
	
}
