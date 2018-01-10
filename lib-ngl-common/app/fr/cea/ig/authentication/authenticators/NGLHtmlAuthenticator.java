package fr.cea.ig.authentication.authenticators;

import javax.inject.Inject;

import com.typesafe.config.Config;

import play.data.FormFactory;
import play.inject.Injector;
import play.libs.concurrent.HttpExecutionContext;
import play.twirl.api.Html;

/**
 * Custom rendering of HTML authentication. This is not needed as the view can be
 * be configured using configuration but it's still a proof of concept. 
 * 
 * @author vrd
 *
 */
public class NGLHtmlAuthenticator extends HtmlAuthenticator {

	/**
	 * DI constructor.
	 * @param config               configuration
	 * @param injector             injector
	 * @param formFactory          form factory
	 * @param httpExecutionContext HTTP execution context
	 */
	@Inject
	public NGLHtmlAuthenticator(Config config, Injector injector, FormFactory formFactory,
			HttpExecutionContext httpExecutionContext) {
		super(config, injector, formFactory, httpExecutionContext);
	}

	/**
	 * Returns the HTML authentication form.
	 */
	@Override
	public Html getDefaultLoginForm(String message) { 
		return views.html.nglAuthFormHtml.render(message);
	}

}
