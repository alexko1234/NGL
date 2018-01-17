package fr.cea.ig.lfw;

import javax.inject.Inject;
import javax.inject.Singleton;

import akka.actor.ActorSystem;
import jsmessages.JsMessagesFactory;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.inject.Injector;

@Singleton
public class LFWApplication {

	/**
	 * Injector.
	 */
	private final Injector injector;
	
	/**
	 * I18N.
	 */
	private final MessagesApi messagesApi;

	/**
	 * Javascript side messages.
	 */
	private final JsMessagesFactory jsMessagesFactory;

	/**
	 * Form factory.
	 */
	private final FormFactory formFactory;
		
	/**
	 * Akka ActorSystem.
	 */
	private final ActorSystem actorSystem;
	
	private final LFWConfig config;
	
	@Inject
	public LFWApplication(Injector injector, 
			              MessagesApi messagesApi, 
			              JsMessagesFactory jsMessagesFactory, 
			              FormFactory formFactory,
			              ActorSystem actorSystem,
			              LFWConfig config) {
		this.injector          = injector;
		this.messagesApi       = messagesApi;
		this.jsMessagesFactory = jsMessagesFactory;
		this.formFactory       = formFactory;
		this.actorSystem       = actorSystem;
		this.config            = config;
	}
	
	protected LFWApplication(LFWApplication app) {
		this(app.injector(),
			 app.messagesApi(),
			 app.jsMessagesFactory(),
			 app.formFactory(),
			 app.actorSystem(),
			 app.lfwConfig());
	}
	
	public Injector injector() { return injector; }
	public MessagesApi messagesApi() { return messagesApi; }
	public JsMessagesFactory jsMessagesFactory() { return jsMessagesFactory; }
	public FormFactory formFactory() { return formFactory; }
	public ActorSystem actorSystem() { return actorSystem; } 
	public LFWConfig lfwConfig() { return config; }
	
}
