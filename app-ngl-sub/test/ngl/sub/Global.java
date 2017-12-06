package ngl.sub;

import play.Application;

public class Global {
	
	public static Application devapp() { 
		return fr.cea.ig.play.test.DevAppTesting.devapp("conf/ngl-sub-test.conf","conf/logger.xml");
	}

}
