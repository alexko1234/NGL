
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;

import scala.collection.Seq;
import scala.collection.JavaConverters;

/**
 * Defines the NGL core module that provides the standard bindings.
 * <p>
 * The standard bindings are defined in {@link #nglBindings(Environment, Configuration)} and the
 * application specific by overriding {@link #nglCustomBindings(Environment, Configuration)}.
 * 
 * @author vrd
 *
 */
public class NGLBaseModule extends play.api.inject.Module {
	
	/**
	 * Class specific logger.
	 */
	protected final play.Logger.ALogger logger; 
		
	/**
	 * Construct the module.
	 * @param environment   environment
	 * @param configuration configuration
	 */
	public NGLBaseModule(Environment environment, Configuration configuration) {
		logger = play.Logger.of(getClass());
		// new ConfigChecker().run();
	}

	/**
	 * Defines the standard NGL application core bindings. 
	 * <ul>
	 *   <li>{@link fr.cea.ig.play.IGGlobals}</li>
	 *   <li>{@link controllers.resources.AssetPlugin}</li>
	 *   <li>{@link fr.cea.ig.authentication.IAuthenticator} to {@link fr.cea.ig.authentication.authenticators.ConfiguredAuthenticator}</li>
	 *   <li>application bindings defined by {@link #nglCustomBindings(Environment, Configuration)}</li>
	 *   <li>{@link play.api.modules.spring.SpringPlugin}</li>
	 *   <li>{@link fr.cea.ig.authorization.IAuthorizator} to {@link fr.cea.ig.authorization.authorizators.ConfiguredAuthorizator}</li>
	 * </ul>
	 * @param environment   environment
	 * @param configuration configuration
	 * @return              application bindings
	 */
	public List<Binding<?>> nglBindings(Environment environment, Configuration configuration) {
		List<Binding<?>> bs = new ArrayList<>();
		bs.add(bind(ConfigChecker.class                              ).toSelf().eagerly());
		// Initialize global variables
		bs.add(bind(fr.cea.ig.play.IGGlobals.class                   ).toSelf().eagerly());
		// Initialize assets server
		bs.add(bind(controllers.resources.AssetPlugin.class          ).toSelf().eagerly());
		// Bind authentication to configured
		bs.add(bind(fr.cea.ig.authentication.IAuthenticator.class)
				.to(fr.cea.ig.authentication.authenticators.ConfiguredAuthenticator.class).eagerly());
		// Add application custom bindings, could define some flags for
		// well known bindings
		bs.addAll(nglCustomBindings(environment,configuration));
		// Bind spring as the last thing as it may access uninitialized
		// things otherwise.
		//bs.add(bind(play.api.modules.spring.SpringPlugin.class       ).toSelf().eagerly());
		bs.add(bind(play.api.modules.spring.SpringComponent.class       ).toSelf().eagerly());
		// Bind authorization to configured, user DAO access requires that 
		// we bind this eagerly after the spring plugin. This still fails 
		// as the Spring plugin initialization is not complete despite
		// the injection being complete. The current solution is to
		// use a lazy binding. Spring plugin start should fixed.
		bs.add(bind(fr.cea.ig.authorization.IAuthorizator.class)
				.to(fr.cea.ig.authorization.authorizators.ConfiguredAuthorizator.class)); //.eagerly());
		return bs;
	}

	/**
	 * Application defined bindings, see {@link #nglBindings(Environment, Configuration)} to see binding
	 * list construction.
	 * @param environment   environment
	 * @param configuration configuration
	 * @return              NGL application specific bindings
	 */
	public List<Binding<?>> nglCustomBindings(Environment environment, Configuration configuration) {
		return new ArrayList<>();
	}
	
	/**
	 * This should not be overridden and the application specific bindings
	 * should be added through {@link #nglCustomBindings(Environment, Configuration)}.
	 * The core bindings list is defined in {@link #nglBindings(Environment, Configuration)}.
	 */
	@Override
	public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
		List<Binding<?>> bindings = nglBindings(environment,configuration);
		for (Binding<?> b : bindings)
			logger.debug("binding {}",b);
		// Possibly something better to do from a list...
		return JavaConverters.asScalaIteratorConverter(bindings.iterator()).asScala().toSeq();
	}

}

// This only checks the config file, not resource.
class ConfigChecker {

	private static final play.Logger.ALogger logger = play.Logger.of(ConfigChecker.class);
	
	//
	public ConfigChecker() {
		checkIncludes();
		// Check/display commonly defined values
		// checkPath("ngl.env");
		// checkPath("")
	}
	
	public void checkIncludes() {
		try {
			String configFile = System.getProperty("config.file");
			// This should not happen except if configuration resource is used but this is
			// not NGL strategy.
			if (configFile == null)
				throw new IOException("config file is not defined");
			logger.debug("*****************************************************");
			logger.debug("checking application configuration at '{}'",configFile);
			logger.debug("***************** config check **********************");
			include(new File(configFile));
		} catch (IOException e) {
			logger.error("config file error",e);
		} finally  {
			logger.debug("*****************************************************");
		}
	}
	
	private static Pattern includePat = Pattern .compile("include\\s+\"(\\S+)\"\\s*");
	
	// Recursively check includes, could loop forever if there is a loop
	// in the includes. This then will lock application start and quite fill the log.
	public void include(File f) throws IOException {
		logger.debug("checking '{}'",f);
		if (!f.isFile())
			throw new FileNotFoundException(f.toString());
		Pattern p = includePat; 
		try (BufferedReader r = new BufferedReader(new FileReader(f))) {
			String line;
			while ((line = r.readLine()) != null) {
				Matcher m = p.matcher(line);
				if (m.matches()) {
					String include = m.group(1);
					File includedFile = new File(f.getParent(),include);
					include(includedFile);
				} 
			}
		}
	}

}
