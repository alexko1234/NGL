package fr.cea.ig.play.test;

import play.inject.Bindings;

import java.util.ArrayList;
// import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
//import java.util.function.Consumer;
import java.util.function.Function;

import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.ws.WSClient;

// This is work in progress.

/**
 * Wrapper around the guice application builder. This allows a base configuration
 * to be stored as an application factory. 
 *  
 * @author vrd
 *
 */
public class ApplicationFactory {

	/**
	 * Configuration file name (see @link DevAppTesting}.
	 */
	private final String configFileName;

	/**
	 * Guice application builder modification to apply. 
	 */
	private final List<Function<GuiceApplicationBuilder,GuiceApplicationBuilder>> mods;
	
	/**
	 * Create a factory using a configuration file
	 * @param configFileName configuration file to use
	 */
	public ApplicationFactory(String configFileName) { 
		this.configFileName = configFileName;
		mods                = new ArrayList<>();
	}
	
	// Cloning by constructor
	protected ApplicationFactory(ApplicationFactory f) {
		configFileName = f.configFileName;
		mods           = new ArrayList<>(f.mods);		
	}

	protected ApplicationFactory constructorClone() {
		return new ApplicationFactory(this);
	}
	
	/**
	 * Add a raw guice application builder modification.
	 * @param mod guice builder modification
	 * @return cloned factory with the added modification
	 */
	public ApplicationFactory mod(Function<GuiceApplicationBuilder,GuiceApplicationBuilder> mod) {
		ApplicationFactory f = constructorClone();
		f.mods.add(mod);
		return f;
	}
	
	/**
	 * Returns a cloned application factory that has the given binding added. 
	 * @param t binding point
	 * @param u binding value
	 * @return cloned factory with the binding added
	 */
	public <T,U extends T> ApplicationFactory bind(Class<T> t, Class<U> u) {
		// This is a clone operation, should be declared as such.
		// ApplicationFactory f = new ApplicationFactory(this);
		// f.mods.add(b -> b.overrides(Bindings.bind(t).to(u)));
		// return f;
		return mod(b -> b.overrides(Bindings.bind(t).to(u)));
	}

	/**
	 * Create an application.
	 * @return created application
	 */
	public Application createApplication() {
		return DevAppTesting.devapp(configFileName, mods);
	}
	
	public void ws(Consumer<WSClient> c) {
		DevAppTesting.testInServer(this.createApplication(), c);
	}
	
	public void run(Consumer<Application> c) {
		Application a = createApplication();
		try {
			c.accept(a);
		} finally {
			a.asScala().stop();			
		}
	}
	
	public void runWs(BiConsumer<Application,WSClient> c)  {
		final Application a = createApplication();
		DevAppTesting.testInServer(a,ws -> c.accept(a,ws));
	}
	
}
