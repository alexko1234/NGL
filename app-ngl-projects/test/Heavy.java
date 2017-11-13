
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static fr.cea.ig.play.test.DevAppTesting.*;

import play.Application;

public class Heavy {
	
	// Should be in some global. 
	public static Application devapp() { 
		return fr.cea.ig.play.test.DevAppTesting.devapp("conf/ngl-projects-test.conf","conf/logger.xml");
	}

	@Test
	public void test01() throws Exception {
	    testInServer(devapp(),
	    		ws -> {	    	
	    	      checkRoutes(ws);
	    		});
	}	
	
}

