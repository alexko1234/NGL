package ngl.bi;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static fr.cea.ig.play.test.DevAppTesting.testInServer;
import fr.cea.ig.play.test.RoutesTest;

import play.Application;
import static ngl.bi.Global.devapp;

public class Heavy {
	
	@Test
	public void test01() throws Exception {
	    testInServer(devapp(),
	    		ws -> {	    	
	    			new RoutesTest()
	    			.autoRoutes()
	    			// .ignore("/api/manip")
	    			.run(ws);
	    		});
	}	
	
}


