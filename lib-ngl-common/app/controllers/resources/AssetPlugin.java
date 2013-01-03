package controllers.resources;
/**											Plugin Play Resource Server
*
*@author ydeshayes
*
*Variables in application.conf:
*								devResourceServerUrl = "url of the dev resource server"
*								prodResourceServerUrl = "uurl of the prod resource server"
*							    modeResourceServer = "prod" or "debug"
*/


import play.Play;
import play.Plugin;
import play.Application;
import play.Logger;

public class AssetPlugin extends Plugin {

	private static final String DEV_RESOURCE_SERVER = "devResourceServerUrl";
	private static final String PROD_RESOURCE_SERVER = "prodResourceServerUrl";
	private static final String MODE_RESOURCE_SERVER = "modeResourceServer";
	
	private Application app;

	public static String urlDev;
	public static String urlProd;
	public static String mode = "debug";
	
	public static boolean loadOk = false;
	public static String errorMessage = "";


	public AssetPlugin(Application app)
	{
		super();
		this.app = app;
	}


	 public void onStart() {
		 if(pluginVarVerif() == true)
		 {
			urlDev = app.configuration().getString(DEV_RESOURCE_SERVER);
			urlProd = app.configuration().getString(PROD_RESOURCE_SERVER);
			if(mode == null)
			 	mode  = app.configuration().getString(MODE_RESOURCE_SERVER);
		 }
		 else
			Logger.error(errorMessage);
	 }


	 private boolean pluginVarVerif() {
			if(app.configuration().getString(PROD_RESOURCE_SERVER)==null) {
				errorMessage += "Error: missing argument "+PROD_RESOURCE_SERVER+" in application.conf";
				return false;
			}
			if(app.configuration().getString(DEV_RESOURCE_SERVER)==null) {
				errorMessage += "Error: missing argument "+DEV_RESOURCE_SERVER+" in application.conf";
				return false;
			}
			
			loadOk = true;
			return true;
		}
		
	public static String getServer(){
		if(mode.equals("debug"))
			return urlDev;
		
		return urlProd;
	}
}
