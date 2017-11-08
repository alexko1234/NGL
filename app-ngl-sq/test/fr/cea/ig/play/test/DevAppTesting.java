package fr.cea.ig.play.test;

import play.Application;
import play.Environment;
import play.inject.guice.GuiceApplicationBuilder;

public class DevAppTesting {
	
	static GuiceApplicationBuilder applicationBuilder;
	
	static Application application;
	
	public static Application devapp() {
		if (applicationBuilder == null) {
			// This should fetch the config files from the resources directory
			// or from a preset classpath.
			System.setProperty("config.file", "c:\\projets\\config\\ngl-sq-dev.2.6.conf");
			System.setProperty("logger.file", "c:\\projets\\config\\logger.xml");
			System.setProperty("play.server.netty.maxInitialLineLength", "16384");
			Environment env = new Environment(/*new File("path/to/app"),*//* classLoader,*/ play.Mode.DEV);
		    applicationBuilder = new GuiceApplicationBuilder().in(env);
		    application = applicationBuilder.build();
		    //System.out.println("** injector " + application.injector());
		}
		// return applicationBuilder.build();
		// This does not properly sets the Play.application() instance.
		return application;
	}

}
