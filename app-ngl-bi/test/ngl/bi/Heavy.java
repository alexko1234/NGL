package ngl.bi;

import static fr.cea.ig.play.test.DevAppTesting.testInServer;
import static ngl.bi.Global.devapp;

import org.junit.Test;

import fr.cea.ig.play.test.RoutesTest;

public class Heavy {
	
	@Test
	public void test01() throws Exception {
	    testInServer(devapp(),
	    		ws -> {	    	
	    			new RoutesTest()
	    			.autoRoutes()
	    			//.ignore("/permissions.js")
	    			.run(ws);
	    		});
	}	
	
}


