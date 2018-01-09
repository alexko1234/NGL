package fr.cea.ig.authentication.authenticators;

import javax.inject.Inject;

import com.typesafe.config.Config;

import play.data.FormFactory;
import play.inject.Injector;
import play.libs.concurrent.HttpExecutionContext;
import play.twirl.api.Html;

public class NGLHtmlAuthenticator extends HtmlAuthenticator {

	@Inject
	public NGLHtmlAuthenticator(Config config, Injector injector, FormFactory formFactory,
			HttpExecutionContext httpExecutionContext) {
		super(config, injector, formFactory, httpExecutionContext);
	}

	/**
	 * Returns the HTML authentication form.
	 */
	@Override
	public Html getLoginForm(String message) { 
		return views.html.nglAuthFormHtml.render(message);
	}

}
