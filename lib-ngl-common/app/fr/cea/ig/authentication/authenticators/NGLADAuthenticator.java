package fr.cea.ig.authentication.authenticators;

import javax.inject.Inject;

import com.typesafe.config.Config;

import play.data.FormFactory;
import play.inject.Injector;
import play.libs.concurrent.HttpExecutionContext;
import play.twirl.api.Html;

public class NGLADAuthenticator extends ADAuthenticator {

	@Inject
	public NGLADAuthenticator(Config config, Injector injector, FormFactory formFactory,
			HttpExecutionContext httpExecutionContext) {
		super(config, injector, formFactory, httpExecutionContext);
	}
	
	/**
	 * Custom login form with an error message, null being no error.
	 */
	@Override
	public Html getLoginForm(String message) { 
		return views.html.nglAuthFormAD.render(message);
	}

	
}
