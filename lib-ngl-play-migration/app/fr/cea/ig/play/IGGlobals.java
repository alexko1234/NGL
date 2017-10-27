package fr.cea.ig.play;

import java.util.ArrayList;

import play.Application;
import play.Configuration; // This is already deprecated
import play.Environment;

import play.cache.AsyncCacheApi;

import play.data.DynamicForm;
import play.data.FormFactory;
import play.data.Form;

import play.libs.Akka;
import play.libs.ws.WSClient;

import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.i18n.Lang;

import play.inject.Injector;

import akka.actor.ActorSystem;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Allows access to some globals that are hard to removed due to the
 * play application lifecycle and mostly static initializers. 
 * 
 * This should allow smooth migration as removing access to this
 * will trigger compilation errors of access to globals to remove. 
 * 
 * This "works" as long any we do not trigger application() calls
 * through indirect calls. This fails for Akka.system() as this
 * internally relies on application(). 
 * 
 * We should have access to the injector as it is low level enough to be accessed
 * from here.
 * 
 * @author vrd
 * 
 */
@Singleton
public class IGGlobals {
	
	// Requisites could be lowered to the needed set
	// This is started as a component and before anyother that requires 
	// access to globals.
	@Inject
	public IGGlobals(Configuration conf, Environment env, Injector inj) {
		configuration = conf; // app.configuration();
		environment   = env; // app.environment();
		injector      = inj; // app.injector();
	}
	
	/**
	 * Play configuration.
	 */
	private static Configuration configuration;
	
	/**
	 * Play environment.
	 */
	private static Environment environment;
	
	/**
	 * Play injector.
	 */
	private static Injector injector;
	
	/**
	 * Play configuration. 
	 * @return Play configuration
	 */
	public static Configuration configuration() {
		return assertInitialized("configuration",configuration);
	}
	
	/**
	 * Play environment.
	 * @return Play environment
	 */
	public static Environment environment() {
		return assertInitialized("environment",environment);
	}
	
	/**
	 * Play injector.
	 * @return Play injector
	 */
	public static Injector injector() {
		return assertInitialized("injector",injector);
	}
	
	/**
	 * Throw a runtime exception if a null value is passed as t.
	 * @param name name of the static field to check
	 * @param t value of the static field to check
	 * @return t if it's not null
	 */
	private static <T> T assertInitialized(String name, T t) {
		if (t == null)
			throw new RuntimeException("IGGlobals are not intiailzed");
		return t;
	}
	
	// -------------------------------------
	// Implementation of methods that are removed from play but still needed by 
	// static methods.
	
	public static FormFactory formFactory() {
		return injector().instanceOf(FormFactory.class);
	}
	
	public static <T> Form<T> form(Class<T> clazz) {
		return formFactory().form(clazz);
	}

	// Need from() factory method
	public static DynamicForm form() {
		return formFactory().form();
	}
	
	public static MessagesApi messagesApi() {
		return injector().instanceOf(MessagesApi.class);
	}
	
	// TODO: possibly use some httpcontext, maybe some lang at least
	public static Messages messages() {
		return messagesApi().preferred(new ArrayList<Lang>());
	}
	
	public static ActorSystem akkaSystem() {
		return injector().instanceOf(ActorSystem.class);
	}
	
	public static AsyncCacheApi cache() {
		return injector().instanceOf(AsyncCacheApi.class);
	}
	
	public static WSClient ws() {
		return injector().instanceOf(WSClient.class);
	}
	
}
