package ngl.sq;

import static fr.cea.ig.play.test.JsonHelper.*;
import static fr.cea.ig.play.test.DevAppTesting.testTimeKey;

import com.fasterxml.jackson.databind.JsonNode;

// More like helpers atm
public class SamplesTest {

	
	/**
	 * Generate a fresh JSON sample data from template 00.
	 * @param  code
	 * @return creation ready json node
	 */
	public static JsonNode create_00(String code) {
		JsonNode n = getJson("data/sample_00");
		remove(n,"_id");
		remove(n,"traceInformation");
		set(n,code,"code");
		return n;
	}
	
}
