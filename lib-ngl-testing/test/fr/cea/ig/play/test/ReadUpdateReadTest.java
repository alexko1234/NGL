package fr.cea.ig.play.test;

import static fr.cea.ig.play.test.DevAppTesting.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.function.Consumer;
import java.util.function.BiConsumer;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;
import play.mvc.Http.Status;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;


/**
 * Fluent java definition of a read,update and read test for a given url.
 * 
 * @author vrd
 *
 */
public class ReadUpdateReadTest {

	/**
	 * Logger.
	 */
	private static final play.Logger.ALogger logger = play.Logger.of(ReadUpdateReadTest.class);
	
	/**
	 * URL to test.
	 */
	private String url;

	private Consumer<JsonNode> beforeUpdate;
	
	private Consumer<JsonNode> beforeAssertion;
	
	private BiConsumer<JsonNode,JsonNode> assertion;
	
	
	public ReadUpdateReadTest(String url) {
		this.url        = url;
		beforeUpdate    = j -> { };
		beforeAssertion = j -> { };
		assertion       = (jOld,jNew) -> { };
	}
	
	/**
	 * Run test using the provided web client.
	 * @param ws web client to use to run the test.
	 */
	public void run(WSClient ws) {
		// Read json from URL
		logger.debug("GET - " + url);
		WSResponse r0 = get(ws,url,Status.OK);
		JsonNode js0 = Json.parse(r0.getBody());
		// Apply modification to read json
		beforeUpdate.accept(js0);
		// Update
		logger.debug("PUT - " + url);		
		WSResponse r1 = put(ws,url,js0.toString(),Status.OK);
		// Read updated data
		logger.debug("GET - " + url);
		WSResponse r2 = get(ws,url,Status.OK);
		JsonNode js1 = Json.parse(r2.getBody());
		// apply precheck to js0 and js1
		beforeAssertion.accept(js0);
		beforeAssertion.accept(js1);
		// assertEquals(js0,js1);
		// cmp("",js0,js1);
		assertion.accept(js0, js1);
	}
	
	/**
	 * Add an assertion to this test.
	 * @param assertion assertion to add
	 * @return          this to chain fluent calls
	 */
	public ReadUpdateReadTest assertion(BiConsumer<JsonNode,JsonNode> assertion) {
		this.assertion = this.assertion.andThen(assertion);
		return this;
	}
	
	/**
	 * Add an alteration to the JSON data before the update request is done.
	 * @param modify JSON alteration to add
	 * @return       this to chain fluent calls
	 */
	public ReadUpdateReadTest beforeUpdate(Consumer<JsonNode> modify) {
		beforeUpdate = beforeUpdate.andThen(modify);
		return this;
	}
	
	public static BiConsumer<JsonNode,JsonNode> notEqualsPath(String... path) {
		return new BiConsumer<JsonNode,JsonNode>() {
			public void accept(JsonNode jOld, JsonNode jNew) {
				assertNotEquals(String.join("/", path),get(jOld,path),get(jNew,path));
			}
		};
	}
	
	public static final BiConsumer<JsonNode,JsonNode> notEqualsTrace =
			(jOld,jNew) -> { notEqualsPath("traceInformation"); };
	
}

