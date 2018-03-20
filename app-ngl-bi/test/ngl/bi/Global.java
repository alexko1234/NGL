package ngl.bi;

import fr.cea.ig.ngl.test.TestAppAuthFactory;
import play.Application;
import rules.services.Rules6Component;

public class Global {
	
	public static final TestAppAuthFactory af = new TestAppAuthFactory("ngl-bi.test.conf"); 
	
	public static Application devapp() { 
//		return fr.cea.ig.play.test.DevAppTesting.devapp("ngl-bi.test.conf");
		return af.createApplication();
	}

}
