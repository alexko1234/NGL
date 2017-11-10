package mongotest;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import static play.mvc.Http.Status;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

import play.Application;
import play.mvc.Result;
import play.mvc.Http.RequestBuilder;
import play.test.Helpers;

import static play.test.Helpers.*;
import play.test.*;
import play.libs.Json;
import play.libs.ws.*;
import java.util.concurrent.CompletionStage;
import static fr.cea.ig.play.test.DevAppTesting.*;
import java.util.function.Consumer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;


// Infrastructure for ngl testing through routes
//
// One of the first stage would be to clear data that is related to testing
// Using the mongo client we would delete all the data whose name contains TEST or 
// something along those lines. The other way around is to have the mongo client
// use a mocked db.
// 
public class Scripted {

	public static Application devapp() {
		return fr.cea.ig.play.test.DevAppTesting.devapp();
	}

	// @Test
	public void testBadRoute() {
		Application app = devapp();
	    RequestBuilder request = Helpers.fakeRequest()
	            .method(GET)
	            .uri("/xx/Kiwi");

	    Result result = route(app, request);
	    assertEquals(Status.NOT_FOUND, result.status());
	}
	
	//@Test
	public void testGoodRoute() {
		Application app = devapp();
		
		controllers.experiments.tpl.Experiments exps = app.injector().instanceOf(controllers.experiments.tpl.Experiments.class);
		Result r = exps.get("CHIP-MIGRATION-20170915_144939CDA");
		assertEquals(Status.OK, r.status());
		
	    RequestBuilder request = Helpers.fakeRequest()
	            .method(GET)
	            .uri("/experiments/CHIP-MIGRATION-20170915_144939CDA");

	    Result result = route(app, request);
	    assertEquals(Status.OK, result.status());
	}
	
	
	// @Test
	public void test00() throws Exception {
	    TestServer server = testServer(3333,devapp());
	    running(server, () -> {
	        try (WSClient ws = WSTestClient.newClient(3333)) {
	            // CompletionStage<WSResponse> completionStage = ws.url("/api/experiments/CHIP-MIGRATION-20170915_144939CDA").get();
	        	CompletionStage<WSResponse> completionStage = ws.url("/experiments/CHIP-MIGRATION-20170915_144939CDA").get();
	            WSResponse response = completionStage.toCompletableFuture().get();
	            assertEquals(OK, response.getStatus());
	            System.out.println(response.getBody());
	        } catch (Exception e) {
	            play.Logger.error(e.getMessage(), e);
	        	// throw e;
	        }
	    });
	}
	
	@Test
	public void test01() throws Exception {
	    testInServer(ws -> {
	    	// WSResponse r0 = get(ws,"/api/experiments/CHIP-MIGRATION-20170915_144939CDA");
	    	// assertEquals(OK, r0.getStatus());
	    	
	    	// Assuming that we have a json response from server that that the get/put
	    	// urls are properly defined, we provide a json alteration function that is
	    	// compared to the get after the put. The other way around is to assert that modified values
	    	// in the input are stored and thus access the values by path and not do a full comparison.
	    	// get(ws,"/processes/search/home",OK);
	    	checkRoutes(ws);
	    	
	    	if (true) {
	    	rur(ws,"/api/experiments/CHIP-MIGRATION-20170915_144939CDA",
	    			js -> { /*((ObjectNode)js).set("typeCode",new TextNode("chip-migration-" + System.currentTimeMillis()));*/ },
	    			js -> { remove(js,"traceInformation");
	    					remove(js,"status","date");
	    			});
	    	rur(ws,"/api/containers/29J81XXL4",
	    			js -> {},
	    			js -> { remove(js,"traceInformation"); });
	    	rur(ws,"/api/samples/AAAA-A120_ST147_T0_A",
	    			js -> {},
	    			js -> { remove(js,"traceInformation"); });
	    	}
	    	
	    	/*
	    	// Fetch body as json tree, update and put modified stuff.
	    	JsonNode jsn = Json.parse(r0.getBody());
	    	System.out.println("******* " + jsn.getNodeType());
	    	for (String s : new String[] { "_id","code","typeCode" }) {
	    		System.out.println("******* " + s + " : " + jsn.path(s)); // jsn.path("traceInformation").path("modifyUser") + "'");
	    	}
	    	System.out.println("******* " + jsn.path("traceInformation").path("modifyUser"));
	    	
	    	WSResponse r1 = get(ws,"/api/experiments/THIS_PROBABLY_DOES_NOT_EXIST");
	    	assertEquals(NOT_FOUND, r1.getStatus());
	    	*/
	    	
	    	// WSResponse r2 = get(ws,"/api/experiments/THIS_PROBABLY_DOES_NOT_EXIST");
	    	// assertEquals(OK, r2.getStatus());
	    	// 
	    });
	}
	
	// @Test
	public void runInBrowser() {
		TestServer server = testServer(3333,devapp());
	    running(server, HTMLUNIT, browser -> {
	        browser.goTo("/");
	        assertEquals("Welcome to Play!", browser.$("#title").text());
	        browser.$("a").click();
	        assertEquals("login", browser.url());
	    });
	}
	
}
