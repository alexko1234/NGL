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

	// Should be in some global. 
	public static Application devapp() { 
		return fr.cea.ig.play.test.DevAppTesting.devapp("conf/ngl-sq-test.conf","conf/logger.xml");
	}

	/**
	 * Super heavy all in one test.
	 * @throws Exception
	 */
	@Test
	public void test01() throws Exception {
	    testInServer(devapp(),
	    		ws -> {	    	
	    	// Assuming that we have a json response from server that that the get/put
	    	// urls are properly defined, we provide a json alteration function that is
	    	// compared to the get after the put. The other way around is to assert that modified values
	    	// in the input are stored and thus access the values by path and not do a full comparison.
	    	// get(ws,"/processes/search/home",OK);
	    	checkRoutes(ws);
	    	
	    	// RUR tests are currently not modifying anything as we use the
	    	// RUR method with the default values.
	    	// Echantillons - samples
	    	rur(ws,"/api/samples/AAAA-A120_ST147_T0_A");
	    	// Supports - supports
	    	rur(ws,"/api/supports/2A4F4FL2H");
	    	// Containers - containers
	    	rur(ws,"/api/containers/HLMF5BBXX_8");
		    rur(ws,"/api/containers/29J81XXL4");
		    // Processus - processes 
		    rur(ws,"/api/processes/BUK_AAAA_METAGENOMIC-PROCESS-WITH-SPRI-SELECT_2A4E2L2AK");
		    // Experiences - experiments
		    rur(ws,"/api/experiments/CHIP-MIGRATION-20170915_144939CDA",
		    	js -> { },
		    	js -> { remove(js,"traceInformation");
		    	        remove(js,"status","date");
		    	      }
		    	);
	    	
	    	
	    });
	}
	
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
	
}
