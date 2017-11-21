package ngl.sq;

import static fr.cea.ig.play.test.JsonHelper.*;
import static fr.cea.ig.play.test.DevAppTesting.testTimeKey;

import com.fasterxml.jackson.databind.JsonNode;

import fr.cea.ig.play.test.JsonFacade;

// More like helpers atm
public class TestSample {

	/**
	 * Generate a fresh JSON sample data from template 00.
	 * @param  code
	 * @return creation ready json node
	 */
	public static JsonNode create_00(String code) {
		return JsonFacade.getJsonFacade("data/sample_00")
				.delete("_id")
				.delete("traceInformation")
				.set("code",code)
				.jsonNode();
	}
	
}
