
package ngl.sq;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import static fr.cea.ig.play.test.DevAppTesting.code;
import static fr.cea.ig.play.test.DevAppTesting.rurNeqTraceInfo;
import static fr.cea.ig.play.test.DevAppTesting.testInServer;
import static fr.cea.ig.play.test.RoutesTest.checkRoutes;
import static fr.cea.ig.play.test.WSHelper.get;

import static ngl.sq.Global.devapp;

import static play.test.Helpers.OK;

public class Heavy {
	@Test
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
	}
}
