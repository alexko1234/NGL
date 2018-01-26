package ngl.sub;

import java.util.function.Function;

//import fr.cea.ig.play.test.ApplicationFactory;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

public class Global {
	
	// public static final ApplicationFactory devapp = new ApplicationFactory("ngl-sub.test.conf"); 
	
	@SafeVarargs
	public static Application devapp(Function<GuiceApplicationBuilder,GuiceApplicationBuilder>... mods) { 
		return fr.cea.ig.play.test.DevAppTesting.devapp("ngl-sub.test.conf",mods);
	}

}
