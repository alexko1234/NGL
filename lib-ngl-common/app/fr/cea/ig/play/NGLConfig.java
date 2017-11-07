package fr.cea.ig.play;

import javax.inject.Inject;
import javax.inject.Singleton;

import fr.cea.ig.play.IGConfig;

/**
 * Provides named accessors on top of the config.
 * 
 * @author vrd
 *
 */
@Singleton
public class NGLConfig {

	/**
	 * Undefined string when a string value is not found at a given path. 
	 */
	public static final String UNDEFINED_STRING = "#UNDEFINED#";
	
	/**
	 * NGL configuration path.
	 */
	public static final String NGL_ENV_PATH = "ngl.env";
	
	/**
	 * Valid values for the NGL_ENV_PATH value.
	 */
	public static final String[] NGL_ENV_VALUES = { "DEV", "PROD" };
	
	/**
	 * Configuration to use.
	 */
	private final IGConfig config;

	
	@Inject
	public NGLConfig(IGConfig config) {
		this.config = config;
	}

	/**
	 * NGL environment, typically DEV or PROD, see NGL_ENV_VALUES. 
	 * @return
	 */
	public String nglEnv() {
		return config.getCheckedString(NGL_ENV_PATH,NGL_ENV_VALUES);
	}
	
	public boolean isNGLEnvProd() {
		return "PROD".equals(nglEnv());
	}
	
	public boolean isNGLEnvDev() {
		return "DEV".equals(nglEnv());
	}
	
}
