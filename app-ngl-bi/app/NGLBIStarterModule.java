
import play.api.Configuration;
import play.api.Environment;

/**
 * NGL BI application start module.
 * 
 * @author vrd
 *
 */
public class NGLBIStarterModule extends NGLCommonStarterModule {
	
	/**
	 * Constructor.
	 * @param environment   environment
	 * @param configuration configuration 
	 */
	public NGLBIStarterModule(Environment environment, Configuration configuration) {
		super(environment,configuration);
		logger.debug("created module " + this);
		logger.info("starting NGL-BI");
		enableDrools();
	}

}


/*
public class NGLBIStarterModule extends play.api.inject.Module {
	
	private static final play.Logger.ALogger logger = play.Logger.of(NGLBIStarterModule.class);

	public NGLBIStarterModule(Environment environment, Configuration configuration) {
		logger.debug("created module " + this);
		logger.info("starting NGL-BI");
		
	}

	// 0:fr.cea.ig.authentication.AuthenticatePlugin
	// 1:controllers.resources.AssetPlugin
	@Override
	public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
		logger.debug("bindings are requested for module " + this);
		return seq(
				bind(fr.cea.ig.play.IGGlobals.class                   ).toSelf().eagerly(),
				bind(controllers.resources.AssetPlugin.class          ).toSelf().eagerly(),				
				bind(fr.cea.ig.authentication.IAuthenticator.class)
				  .to(fr.cea.ig.authentication.authenticators.ConfiguredAuthenticator.class).eagerly(),
				bind(fr.cea.ig.authorization.IAuthorizator.class)
				  .to(fr.cea.ig.authorization.authorizators.ConfiguredAuthorizator.class).eagerly(),

				bind(StaticInitComponent.class                        ).toSelf().eagerly(),
				bind(play.modules.jongo.MongoDBPlugin.class           ).toSelf().eagerly(),
				// bind(play.modules.jongo.MongoDBPlugin.class           ).toSelf().eagerly(),
				bind(play.modules.mongojack.MongoDBPlugin.class       ).toSelf().eagerly(),
				bind(rules.services.Rules6Component.class             ).toSelf().eagerly(),
				// Force JsMessages init
				// bind(controllers.main.tpl.Main.class                  ).toSelf().eagerly(),
				bind(play.api.modules.spring.SpringPlugin.class       ).toSelf().eagerly()
				);
	}
	
}
*/

/*
class StaticInitComponent {
	public StaticInitComponent() {
		// TODO: fix/check disabled date formatting 
		// play.data.format.Formatters.register(Date.class,new DateFormatter("yyyy-MM-dd"));
	}
    public static class DateFormatter extends Formatters.SimpleFormatter<Date> {
        
        private final String pattern;
        
        / **
         * Creates a date formatter.
         *
         * @param pattern date pattern, as specified for {@link SimpleDateFormat}.
         * /
        public DateFormatter(String pattern) {
            this.pattern = pattern;
        }
        
        / **
         * Binds the field - constructs a concrete value from submitted data.
         *
         * @param text the field text
         * @param locale the current <code>Locale</code>
         * @return a new value
         * /
        public Date parse(String text, Locale locale) throws java.text.ParseException {
            if(text == null || text.trim().isEmpty()) {
                return null;
            }
            try{
            	Long l = Long.valueOf(text);
            	return new Date(l);
            }catch(NumberFormatException e){
            	SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
                sdf.setLenient(false);  
                return sdf.parse(text);
            }
            
            
        }
        
        / **
         * Unbinds this fields - converts a concrete value to a plain string.
         *
         * @param value the value to unbind
         * @param locale the current <code>Locale</code>
         * @return printable version of the value
         * /
        public String print(Date value, Locale locale) {
            if(value == null) {
                return "";
            }
            return new SimpleDateFormat(pattern, locale).format(value);
        }
        
    }

}
*/
