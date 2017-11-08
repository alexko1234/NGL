package fr.cea.ig.play;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import play.i18n.Lang;
import play.i18n.MessagesApi;
import play.i18n.Messages;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.data.Form;

import fr.cea.ig.play.NGLConfig;


// TODO: clean, comment

/**
 * Class to help transition from the old style globals to new style
 * DI. An instance of this class is supposed to be injected instead of
 * using IGGlobals.
 * 
 * @author vrd
 *
 */
@Singleton
public class NGLTools {

	private static final play.Logger.ALogger logger = play.Logger.of(NGLTools.class);
	
	private final NGLConfig config;
	private final MessagesApi messagesApi;
	private final FormFactory formFactory;
	
	@Inject
	public NGLTools(NGLConfig config,
			MessagesApi messagesApi,
			FormFactory formFactory) {
		this.config   = config;
		this.messagesApi = messagesApi;
		this.formFactory = formFactory;
	}
		
	public NGLConfig config() { 
		return config;
	}
	
	public String message(String key) {
		return messages().at(key);
		// return play.api.i18n.Messages.get(key);
		// return "Messages(" + key + ")";
	}
	
	public String messageEnv(String key) {
		if (config.isNGLEnvProd()) 
			return message(key);
		return message(key) + "-" + config.nglEnv();
	}
	
	public String currentUser() {
		return fr.cea.ig.authentication.Helper.username(play.mvc.Http.Context.current().session());
	}
	
	public Messages messages() {
		logger.debug("messages(");
		return messagesApi.preferred(new ArrayList<Lang>());
		// return Messages;
	}
	
	public FormFactory formFactory() {
		return formFactory;
	}
	
	public <T> Form<T> form(Class<T> clazz) {
		return formFactory().form(clazz);
	}

	public DynamicForm form() {
		return formFactory().form();
	}

}
