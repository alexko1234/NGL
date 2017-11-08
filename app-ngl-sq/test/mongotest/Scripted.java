package mongotest;

import static org.junit.Assert.assertEquals;
import static fr.cea.ig.play.test.WSHelper.*;
import static fr.cea.ig.play.test.DevAppTesting.*;

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

// TODO: move and rename
public class Scripted {

	// Should be in some global. 
	public static Application devapp() { 
		return ngl.sq.Global.devapp();
	}

	/**
	 * Super heavy all in one test.
	 * @throws Exception
	 */
	/*@Test
	public void test01() throws Exception {
		JsonNode sample_0 = ngl.sq.SamplesTest.create_00(code("TEST"));
		// if (true) throw new RuntimeException("" + sample_0.path("code"));
		
	    testInServer(devapp(),
	    		ws -> {	    	
	    			
	    	// Assuming that we have a json response from server that that the get/put
	    	// urls are properly defined, we provide a json alteration function that is
	    	// compared to the get after the put. The other way around is to assert that modified values
	    	// in the input are stored and thus access the values by path and not do a full comparison.
	    	checkRoutes(ws);
	    	
	    	// RUR tests are currently not modifying anything as we use the
	    	// RUR method with the default values so those are barely tests. 
	    	// The confirmation of success could come from the update date that would be different.
	    	// We use the object names to trigger the view template and the rur.
	    	
	    	// Echantillons - samples
	    	rurNeqTraceInfo("/api/samples/AAAA-A120_ST147_T0_A",ws);
	    	get(ws,"/samples/AAAA-A120_ST147_T0_A",OK);
	    	
	    	// Supports - supports
	    	rurNeqTraceInfo("/api/supports/2A4F4FL2H",ws);
	    	get(ws,"/supports/2A4F4FL2H",OK);
	    	
	    	// Containers - containers
	    	rurNeqTraceInfo("/api/containers/HLMF5BBXX_8",ws);
		    rurNeqTraceInfo("/api/containers/29J81XXL4",ws);
		    get(ws,"/containers/29J81XXL4",OK);
		    
		    // Processus - processes 
		    rurNeqTraceInfo("/api/processes/BUK_AAAA_METAGENOMIC-PROCESS-WITH-SPRI-SELECT_2A4E2L2AK",ws);
		    // This is a 404
		    // get(ws,"/processes/BUK_AAAA_METAGENOMIC-PROCESS-WITH-SPRI-SELECT_2A4E2L2AK",OK);
		    
		    // Experiences - experiments
		    rurNeqTraceInfo("/api/experiments/CHIP-MIGRATION-20170915_144939CDA",ws);
	    	get(ws,"/experiments/CHIP-MIGRATION-20170915_144939CDA",OK);
	    	
	    	// Create container  - create and get code/id
	    	// probably /api/containers
	    	
	    	// Create experiment - create and get code/id
	    	
	    	
	    });
	}*/
	
	/*
	 * Browser level testing does not seem to be at the 
	 * proper granularity level. 
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
	*/
	
	@Test
	public void testInServer() throws Exception {
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
	
}
