
package ngl.sq;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import static fr.cea.ig.play.test.DevAppTesting.newCode;
import static fr.cea.ig.play.test.DevAppTesting.cr;
import static fr.cea.ig.play.test.DevAppTesting.rurNeqTraceInfo;
import static fr.cea.ig.play.test.DevAppTesting.testInServer;
import static fr.cea.ig.play.test.DevAppTesting.savage;
import static fr.cea.ig.play.test.RoutesTest.checkRoutes;
import static fr.cea.ig.play.test.WSHelper.get;

import static ngl.sq.Global.devapp;

import static play.test.Helpers.OK;
import static play.test.Helpers.NOT_FOUND;

import models.laboratory.container.instance.Container;

public class Heavy {
	
	// @Test
	public void test01() throws Exception {
		
	    testInServer(devapp(),
	    		ws -> {	    	
	    			JsonNode sample_0 = ngl.sq.SampleFactory.create_00(newCode());
	    			JsonNode container_0 = ngl.sq.ContainerFactory.create_00(newCode(),sample_0);
	    			// if (true) throw new RuntimeException("" + sample_0.path("code"));
	    			
	    			get(ws,"/",OK);
	    			get(ws,"/404",NOT_FOUND);
	    	    	cr(ws,"/api/samples",sample_0);
	    	    	rurNeqTraceInfo(ws,"/api/samples/",sample_0);
	    	    	// cr(ws,"/api/containers",container_0);
	    	    	// We need to use the internal api, using directly the application.
	    	    	// This accesses pretty directly to the mongo layer as this is all
	    	    	// we've got in this lib. ngl-common test should provide a more controlled
	    	    	// access.
	    	    	savage(container_0,Container.class,models.utils.InstanceConstants.CONTAINER_COLL_NAME);
	    	    	rurNeqTraceInfo(ws,"/api/containers/",container_0);
	    	    	if (true) return; // throw new RuntimeException("ok");
	    	    	

	    			
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
	    	rurNeqTraceInfo(ws,"/api/samples/AAAA-A120_ST147_T0_A");
	    	get(ws,"/samples/AAAA-A120_ST147_T0_A",OK);
	    	
	    	// Supports - supports
	    	rurNeqTraceInfo(ws,"/api/supports/2A4F4FL2H");
	    	get(ws,"/supports/2A4F4FL2H",OK);
	    	
	    	// Containers - containers
	    	//rurNeqTraceInfo("/api/containers/HLMF5BBXX_8",ws);
		    rurNeqTraceInfo(ws,"/api/containers/29J81XXL4");
		    get(ws,"/containers/29J81XXL4",OK);
		    
		    // Processus - processes 
		    rurNeqTraceInfo(ws,"/api/processes/BUK_AAAA_METAGENOMIC-PROCESS-WITH-SPRI-SELECT_2A4E2L2AK");
		    // This is a 404
		    // get(ws,"/processes/BUK_AAAA_METAGENOMIC-PROCESS-WITH-SPRI-SELECT_2A4E2L2AK",OK);
		    
		    // Experiences - experiments
		    rurNeqTraceInfo(ws,"/api/experiments/CHIP-MIGRATION-20170915_144939CDA");
	    	get(ws,"/experiments/CHIP-MIGRATION-20170915_144939CDA",OK);
	    	
	    	// Create container  - create and get code/id
	    	// probably /api/containers
	    	
	    	// Create experiment - create and get code/id
	    	
	    	
	    });
	}
}
