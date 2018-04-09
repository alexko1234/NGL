package ngl.sq;

import java.util.function.Function;

import controllers.instruments.io.common.novaseq.OutputImplementationSwitch;
import fr.cea.ig.ngl.test.TestAppAuthFactory;
// import fr.cea.ig.ngl.test.authentication.Identity;
// import fr.cea.ig.play.test.ApplicationFactory;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

public class Global {

	public static final TestAppAuthFactory af = new TestAppAuthFactory("ngl-sq.test.conf");
	
	// intrument.io factories
	public static final TestAppAuthFactory afOld = af;
	public static final TestAppAuthFactory afNew = af.override(OutputImplementationSwitch.class, OutputImplementationSwitch.New.class);
	
	@SafeVarargs
	public static Application devapp(Function<GuiceApplicationBuilder,GuiceApplicationBuilder>... mods) { 
		return fr.cea.ig.play.test.DevAppTesting.devapp("ngl-sq.test.conf",mods);
	}
	
}

