package ngl.sq;

import static fr.cea.ig.play.test.DevAppTesting.cr;
import static fr.cea.ig.play.test.DevAppTesting.newCode;
import static fr.cea.ig.play.test.DevAppTesting.rurNeqTraceInfo;
import static fr.cea.ig.play.test.DevAppTesting.testInServer;
import static ngl.sq.Global.devapp;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

public class SamplesTest {

	@Test
	public void testCreation_00() {
		testInServer(devapp(),
				ws -> {	    	
					JsonNode sample_0 = ngl.sq.SampleFactory.create_00(newCode());
	    	    	cr(ws,"/api/samples",sample_0);
	    	    	rurNeqTraceInfo(ws,"/api/samples/",sample_0);
				});
	}

	
}
