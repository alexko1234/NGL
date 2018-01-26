package fr.cea.ig.play.test;

import play.inject.Bindings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//import java.util.function.Consumer;
import java.util.function.Function;

import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

// This is work in progress.

/**
 * Wrapper around the guice application builder. 
 *  
 * @author vrd
 *
 */
public class ApplicationFactory {

	// We need some application singleton as there can only be one application at a time.
	
	private final String configFileName;
	
	// This is applied to the guice application builder.
	// 
	private final List<Function<GuiceApplicationBuilder,GuiceApplicationBuilder>> mods;
	
	@SafeVarargs
	public ApplicationFactory(String configFileName, Function<GuiceApplicationBuilder,GuiceApplicationBuilder>... ms) { 
		this.configFileName = configFileName;
		mods                = Arrays.asList(ms);
	}
	
	public ApplicationFactory(ApplicationFactory f) {
		configFileName = f.configFileName;
		mods = new ArrayList<>(f.mods);
	}
	
	public Application createApplication() {
		return DevAppTesting.devapp(configFileName, mods);
	}
	
	/*public ApplicationFactory apply(Consumer<Builder>... mods) {
		for (Consumer<Builder> mod : mods)
			this.mods.add(mod);
		return this;
	}*/
	
	// Return a cloned factory so the factory can be used as some
	// configured base.
	public <T,U extends T> ApplicationFactory bind(Class<T> t, Class<U> u) {
		ApplicationFactory f = new ApplicationFactory(this);
		f.mods.add(b -> b.overrides(Bindings.bind(t).to(u)));
		return f;
	}
	
}
