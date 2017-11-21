package fr.cea.ig.play;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.JsonNode;

import play.i18n.Lang;
import play.i18n.MessagesApi;
import rules.services.RulesServices6;
import play.i18n.Messages;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.data.validation.ValidationError;
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
public class NGLContext {

	private static final play.Logger.ALogger logger = play.Logger.of(NGLContext.class);
	
	private final NGLConfig config;
	private final MessagesApi messagesApi;
	private final FormFactory formFactory;
	
	@Inject
	public NGLContext(NGLConfig   config,
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
		// logger.debug("messages");
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

	public String getInstitute() {
		return config().getInstitute();
	}
	
	public String getRulesKey() {
		return config.getRulesKey();
	}
	
	public List<Object> rulesServices6(String ruleAnnotationName, List<Object> facts) { 
		return RulesServices6.getInstance().callRulesWithGettingFacts(getRulesKey(), ruleAnnotationName, facts);
	}
	
	// ---------------------------------------------------------------------------------------
	// Comes from play.data.Form
	
	/**
	 * Returns form errors serialized as JSON.
	 */
	public JsonNode errorsAsJson(Map<String, List<ValidationError>> errors) {
		return errorsAsJson(play.mvc.Http.Context.current() != null ? play.mvc.Http.Context.current().lang() : null, errors);
	}

	/**
	 * Returns form errors serialized as JSON using the given Lang.
	 * @param  lang   language to use for the errors 
	 * @param  errors errors
	 * @return JSON node built from the given errors
	 */
	public com.fasterxml.jackson.databind.JsonNode errorsAsJson(play.i18n.Lang lang, Map<String, List<ValidationError>> errors) {
		Map<String, List<String>> allMessages = new java.util.HashMap<>();
		errors.forEach((key, errs) -> {
			if (errs != null && !errs.isEmpty()) {
				List<String> messages = new ArrayList<>();
				for (ValidationError error : errs) {
					if (messagesApi != null && lang != null) {
						messages.add(messagesApi.get(lang, error.messages(), translateMsgArg(error.arguments(), messagesApi, lang)));
					} else {
						messages.add(error.message());
					}
				}
				allMessages.put(key, messages);
			}
		});
		return play.libs.Json.toJson(allMessages);
	}

	private Object translateMsgArg(List<Object> arguments, MessagesApi messagesApi, play.i18n.Lang lang) {
		if (arguments != null) {
			return arguments.stream().map(arg -> {
				if (arg instanceof String) {
					return messagesApi != null ? messagesApi.get(lang, (String)arg) : (String)arg;
				}
				if (arg instanceof List) {
					return ((List<?>) arg).stream().map(key -> messagesApi != null ? messagesApi.get(lang, (String)key) : (String)key).collect(Collectors.toList());
				}
				return arg;
			}).collect(Collectors.toList());
		} else {
			return null;
		}
	}

}
