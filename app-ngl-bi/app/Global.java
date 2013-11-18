import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.data.format.Formatters;
import play.mvc.Action;
import play.mvc.Http.Request;


public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		Logger.info("NGL-BI has started");		   	
		Formatters.register(Date.class,new DateFormatter("yyyy-MM-dd"));
	}  

	


	@Override
	public void onStop(Application app) {
		Logger.info("NGL-BI shutdown...");
	}  

	
	
	
	@Override
	public Action onRequest(Request request, Method actionMethod) {
		//if(Integer.valueOf(request.getHeader("Content-Length")).intValue() < (100*1024) ){
			Logger.debug("Request: "+request.body().toString());
		//}
		return new fr.cea.ig.authentication.Authenticate();
	}




	/**
     * Formatter for <code>java.util.Date</code> values.
     * Override the default formatter to manage the date in milliseconds from 1970
     */

    public static class DateFormatter extends Formatters.SimpleFormatter<Date> {
        
        private final String pattern;
        
        /**
         * Creates a date formatter.
         *
         * @param pattern date pattern, as specified for {@link SimpleDateFormat}.
         */
        public DateFormatter(String pattern) {
            this.pattern = pattern;
        }
        
        /**
         * Binds the field - constructs a concrete value from submitted data.
         *
         * @param text the field text
         * @param locale the current <code>Locale</code>
         * @return a new value
         */
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
        
        /**
         * Unbinds this fields - converts a concrete value to a plain string.
         *
         * @param value the value to unbind
         * @param locale the current <code>Locale</code>
         * @return printable version of the value
         */
        public String print(Date value, Locale locale) {
            if(value == null) {
                return "";
            }
            return new SimpleDateFormat(pattern, locale).format(value);
        }
        
    }
}