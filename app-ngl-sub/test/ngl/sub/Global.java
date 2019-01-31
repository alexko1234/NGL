package ngl.sub;

import fr.cea.ig.ngl.test.TestAppAuthFactory;
//import fr.cea.ig.play.test.ApplicationFactory;
import play.Application;

public class Global {
	
	public static final TestAppAuthFactory af = new TestAppAuthFactory("ngl-sub.test.conf");
	
//	@SafeVarargs
//	public static Application devapp(Function<GuiceApplicationBuilder,GuiceApplicationBuilder>... mods) { 
//		return fr.cea.ig.play.test.DevAppTesting.devapp("ngl-sub.test.conf",mods);
//	}

	// @SafeVarargs
	public static Application devapp() { 
		return af.createApplication();
	}

}
