package fr.cea.ig.play;

import javax.inject.Inject;

import play.http.HttpErrorHandler;
import play.mvc.BodyParser;

/**
 * Custom body parsers.
 * 
 * Could provide some custom json parser if the play provided one is still 
 * not using the play defined json parser.
 */
public class IGBodyParsers {
	
	/**
	 * 5 mb buffer json parser.
	 * Allows replacement of annotations @BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	 * by @BodyParser.Of(value = IGBodyParser.Json5MB).
	 */
	public static class Json5MB extends BodyParser.Text {
		
	    @Inject
	    public Json5MB(HttpErrorHandler errorHandler) {
	        super(5 * 1024 * 1024, errorHandler);
	    }
	    
	}
	
	/**
	 * 10 mb buffer json parser.
	 * Allows replacement of annotations @BodyParser.Of(value = BodyParser.Json.class, maxLength = 10000 * 1024)
	 * by @BodyParser.Of(value = IGBodyParser.Json10MB).
	 */
	public static class Json10MB extends BodyParser.Text {
		
	    @Inject
	    public Json10MB(HttpErrorHandler errorHandler) {
	        super(10 * 1024 * 1024, errorHandler);
	    }
	  
	}
	
}
