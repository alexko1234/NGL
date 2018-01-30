package ngl.sq;

import java.util.function.Function;

import fr.cea.ig.ngl.test.TestAppAuthFactory;
import fr.cea.ig.ngl.test.authentication.Identity;
// import fr.cea.ig.play.test.ApplicationFactory;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

public class Global {

	public static final TestAppAuthFactory af = new TestAppAuthFactory("ngl-sq.test.conf");
	
	@SafeVarargs
	public static Application devapp(Function<GuiceApplicationBuilder,GuiceApplicationBuilder>... mods) { 
		return fr.cea.ig.play.test.DevAppTesting.devapp("ngl-sq.test.conf",mods);
	}
	
}

