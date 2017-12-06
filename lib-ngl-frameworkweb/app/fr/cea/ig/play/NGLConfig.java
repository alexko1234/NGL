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
	 * NGL configuration path in configuration.
	 */
	public static final String NGL_ENV_PATH = "ngl.env";
	
	/**
	 * Valid values for the NGL_ENV_PATH value.
	 */
	public static final String[] NGL_ENV_VALUES = { "DEV", "PROD" };
	
	/**
	 * NGL institute path in configuration.
	 */
	public static final String NGL_INSTITUTE_PATH = "institute"; 
	
	public static final String NGL_RULES_KEY = "rules.key";
	
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
	
	public String getInstitute() {
		return config.getString(NGL_INSTITUTE_PATH);
	}
	
	public String getRulesKey() {
		return config.getString(NGL_RULES_KEY);
	}
	
}
