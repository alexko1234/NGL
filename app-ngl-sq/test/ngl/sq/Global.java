package ngl.sq;

import java.util.function.Function;

import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

public class Global {

	@SafeVarargs
	public static Application devapp(Function<GuiceApplicationBuilder,GuiceApplicationBuilder>... mods) { 
		return fr.cea.ig.play.test.DevAppTesting.devapp("ngl-sq.test.conf",mods);
	}
	
}

