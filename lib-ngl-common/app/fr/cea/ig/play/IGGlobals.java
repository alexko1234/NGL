package fr.cea.ig.play;

import play.Configuration; // This is already deprecated
import play.Environment;

/**
 * Allows access to some globals that are hard to removed due to the
 * play application lifecycle and mostly static initializers. 
 * This "works" as long any we do not trigger application() calls
 * through indirect calls. This fails for Akka.system() as this
 * internally relies on application(). 
 * 
 * We should have access to the injector as it is low level enough to be accessed
 * from here.
 */
public class IGGlobals {
	public static Configuration configuration;
	public static Environment environment;
}
