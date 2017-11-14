package ngl.bi;

import play.Application;

public class Global {
	
	public static Application devapp() { 
		return fr.cea.ig.play.test.DevAppTesting.devapp("conf/ngl-bi-test.conf","conf/logger.xml");
	}

}
