
/*
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;

import play.Logger;
import play.libs.Json;

// This is not a custom mapper...
// This is crap initialization code.
// public 
class JavaJsonCustomObjectMapper {
	private static final play.Logger.ALogger logger = Logger.of(JavaJsonCustomObjectMapper.class);
    JavaJsonCustomObjectMapper() {
    	
        ObjectMapper mapper = new ObjectMapper() // Json.newDefaultMapper()
                // enable features and customize the object mapper here ...
                //.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                //.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        		// .disable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)
                ;
        // etc.
        Json.setObjectMapper(mapper);
        logger.debug("set json mapper to " + mapper);
        logger.debug("json mapper through accessor " + Json.mapper());
        // Json.mapper().enable(com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
		if (Json.mapper().isEnabled(com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_INTEGER_FOR_INTS))
			throw new RuntimeException("bad object mapper config " + Json.mapper());
        //logger.info("updated mapper to " + mapper);
        
    }

}
public class JavaJsonCustomObjectMapperModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(JavaJsonCustomObjectMapper.class).asEagerSingleton();
    }

}
*/

