package ngl.sq;

import play.Application;

public class Global {

	public static Application devapp() { 
		return fr.cea.ig.play.test.DevAppTesting.devapp("ngl-sq.test.conf");
	}
	
}

