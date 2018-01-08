package controllers.resources;

import play.Application;
import play.Logger;
import play.Play;

// import play.Plugin;
import javax.inject.Inject;
import javax.inject.Singleton;

// import fr.cea.ig.authentication.AuthenticatePlugin;

@Singleton
public class AssetPlugin { // extends play.Plugin {

	/**
	 * Logger.
	 */
	private static final Logger.ALogger logger = Logger.of(AssetPlugin.class);

	/**
	 * Application.conf key for assets server.
	 */ 
	private static final String ASSET_URL = "asset.url";

	/**
	 * Global definition of the assets server url.
	 */
	private static String url; 
	
	@Inject
	public AssetPlugin(Application app)	{
		super();
		logger.debug("injecting " + app);
		url = app.configuration().getString(ASSET_URL);
		if (url == null)
			logger.error("missing " + ASSET_URL + " in application.conf");
		else
			logger.info("asset url : " + url);
		logger.debug("injected");
	}

	public static String getServer() {
		if (url == null) {
			logger.warn("accessing url that is not set");
			return "assetPathNotSet";
		}
		logger.debug("getServer() : " + url); 
		return url;
	}

}
