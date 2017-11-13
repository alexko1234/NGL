
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static fr.cea.ig.play.test.DevAppTesting.*;

import play.Application;
import static ngl.plates.Global.devapp;

public class Heavy {
	

	@Test
	public void test01() throws Exception {
	    testInServer(devapp(),
	    		ws -> {	    	
	    	      checkRoutes(ws);
	    		});
	}	
	
}


