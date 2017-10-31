package fr.cea.ig.play;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.Callable;

import play.Application;
import play.Configuration; // This is already deprecated
import play.Environment;

import play.cache.SyncCacheApi;

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
 * through indirect calls.
 * 
 * We have access to the injector as it is low level enough to be accessed
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
	public IGGlobals(Configuration conf, Environment env, Injector inj, SyncCacheApi cac) {
		configuration = conf; // app.configuration();
		environment   = env;  // app.environment();
		injector      = inj;  // app.injector();
		cache         = cac;
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
	 * Default cache.
	 */
	private static SyncCacheApi cache;
	
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
	 * Default synchronized cache instance.
	 * @return synchronized cache instance
	 */
	public static SyncCacheApi cache() {
		// return injector().instanceOf(SyncCacheApi.class);
		// NoCache or HashCache resolve the security exception problem.
		// return NoCache.instance();
		// return HashCache.instance();
		return assertInitialized("cache",cache);
	}

	/**
	 * Throw a runtime exception if a null value is passed as t.
	 * @param name name of the static field to check
	 * @param t value of the static field to check
	 * @return t if it's not null
	 */
	private static <T> T assertInitialized(String name, T t) {
		if (t == null)
			throw new RuntimeException("IGGlobals '" + name + "()' is not intiailzed");
		return t;
	}

	// -------------------------------------
	// Implementation of methods that are removed from play but still needed by 
	// NGL static methods.
	
	// TODO: inject
	public static FormFactory formFactory() {
		return injector().instanceOf(FormFactory.class);
	}
	
	public static <T> Form<T> form(Class<T> clazz) {
		return formFactory().form(clazz);
	}

	// TODO: inject
	public static DynamicForm form() {
		return formFactory().form();
	}
	
	public static MessagesApi messagesApi() {
		return injector().instanceOf(MessagesApi.class);
	}
	
	// TODO: possibly use httpcontext, maybe some lang at least
	// TODO: inject
	public static Messages messages() {
		return messagesApi().preferred(new ArrayList<Lang>());
	}
	
	// TODO:inject
	public static ActorSystem akkaSystem() {
		return injector().instanceOf(ActorSystem.class);
	}
	
	// TODO: inject
	public static WSClient ws() {
		return injector().instanceOf(WSClient.class);
	}
	
	/*
	static class NoCache implements SyncCacheApi {
		private  static NoCache instance;
		public static NoCache instance() {
			if (instance == null)
				instance = new NoCache();
			return instance;
		}
		public <T> T get(String key) { 
			return null; 
		}
		public <T> T getOrElseUpdate(String key, Callable<T> block) { 
			try {
				return block.call();
			} catch (Exception e) {
				throw new RuntimeException("block call failed",e);
			}
		}
		public <T> T getOrElseUpdate(String key, Callable<T> block, int expiration) { 
			return getOrElseUpdate(key,block);
		}
		public void remove(String key) {
		}
		public void set(String key, Object value) {
		}
		public void set(String key, Object value, int expiration) {
		}
	}
	
	static class HashCache implements SyncCacheApi {
		private  static HashCache instance;
		public static HashCache instance() {
			if (instance == null)
				instance = new HashCache();
			return instance;
		}
		private Map<String,Object> cache = new HashMap<String,Object>();
		public <T> T get(String key) { 
			return (T)cache.get(key); 
		}
		public <T> T getOrElseUpdate(String key, Callable<T> block) {
			T t = get(key);
			if (t != null)
				return t;
			try {
				t = block.call();
				cache.put(key,t);
				return t;
			} catch (Exception e) {
				throw new RuntimeException("block call failed",e);
			}
		}
		public <T> T getOrElseUpdate(String key, Callable<T> block, int expiration) { 
			return getOrElseUpdate(key,block);
		}
		public void remove(String key) {
			cache.remove(key);
		}
		public void set(String key, Object value) {
			cache.put(key,value);
		}
		public void set(String key, Object value, int expiration) {
			set(key,value);
		}
	}*/
	
}
