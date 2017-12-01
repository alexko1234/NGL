package ngl.sq;

import static ngl.sq.SampleFactory.fresh;
import static ngl.sq.SampleFactory.from;
import static play.mvc.Http.Status.OK;

import java.util.function.Consumer;

import fr.cea.ig.play.test.DBObjectFactory;
import fr.cea.ig.play.test.DevAppTesting;

import com.fasterxml.jackson.databind.JsonNode;

import fr.cea.ig.play.test.JsonHelper;
import fr.cea.ig.play.test.WSHelper;
import models.laboratory.sample.instance.Sample;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

// More like helpers atm
public class SampleFactory extends DBObjectFactory {

	public static final String samplesUrl = "/api/samples";
	public static final String res_00 = "data/sample_00";

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
		return from(resourceName,Sample.class);
	}
	
	public static Sample freshInstance(WSClient ws, String resourceName) {
		Sample sample = fresh(from(resourceName));
		WSResponse r = WSHelper.postObject(ws,samplesUrl,sample,OK);
		return Json.fromJson(Json.parse(r.getBody()), Sample.class);
	}
	
}
