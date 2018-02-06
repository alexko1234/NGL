package ngl.plates;

import fr.cea.ig.ngl.test.TestAppAuthFactory;
import play.Application;

public class Global {
	
	public static final TestAppAuthFactory af = new TestAppAuthFactory("ngl-plates.test.conf"); 
	
	public static Application devapp() { 
		return fr.cea.ig.play.test.DevAppTesting.devapp("ngl-plates.test.conf");
	}

}
