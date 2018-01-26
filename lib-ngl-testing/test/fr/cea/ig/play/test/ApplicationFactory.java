package fr.cea.ig.play.test;

import play.Application;

/**
 * Wrapper around the guice application builder.
 *  
 * @author vrd
 *
 */
public class ApplicationFactory {

	private final String configFileName;
	
	public ApplicationFactory(String configFileName) { 
		this.configFileName = configFileName;
	}
	
	public Application createApplication() {
		throw new RuntimeException("not implemented");
	}
	
}
