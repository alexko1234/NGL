
import play.api.Configuration;
import play.api.Environment;
import services.instance.ImportDataGET;
//import services.instance.play;

/**
 * Common name across NGL applications for the start module.
 *  
 * @author vrd
 *
 */
public class NGLAppModule extends NGLDataStarterModule {
	
	/**
	 * Constructor.
	 * @param environment   environment
	 * @param configuration configuration
	 */
	public static final play.Logger.ALogger logger = play.Logger.of(NGLAppModule.class);
	public NGLAppModule(Environment environment, Configuration configuration) {
		super(environment,configuration);
		logger.error("NGLAppModule");
}

}
