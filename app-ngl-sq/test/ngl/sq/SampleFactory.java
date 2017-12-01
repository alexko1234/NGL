package ngl.sq;

import java.util.function.Consumer;

import fr.cea.ig.play.test.DBObjectFactory;
import fr.cea.ig.play.test.DevAppTesting;

import com.fasterxml.jackson.databind.JsonNode;

import fr.cea.ig.play.test.JsonHelper;
import models.laboratory.sample.instance.Sample;

// More like helpers atm
public class SampleFactory extends DBObjectFactory {

	public static JsonNode apply(JsonNode n, Consumer<Sample> modification) {
		return apply(n,Sample.class,modification);
	}
	
	/**
	 * Generate a fresh JSON sample data from template 00.
	 * @param  code
	 * @return creation ready json node
	 */
	public static JsonNode create_00(String code) {
		return apply(JsonHelper.getJson("data/sample_00"),
				s -> { 
					s._id              = null;
					s.traceInformation = null;
					s.code             = code;
				});
	}
	
	// Puts the sample in a state that allows creation
	public static Sample fresh(Sample s) {
		s._id              = null;
		s.traceInformation = null;
		s.code             = DevAppTesting.newCode();
		return s;
	}
	
	public static Sample from(String resourceName) {
		return create(resourceName,Sample.class);
	}
		
}
