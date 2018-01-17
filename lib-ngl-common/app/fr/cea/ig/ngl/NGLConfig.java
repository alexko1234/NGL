package fr.cea.ig.ngl;

import java.util.List;

import javax.inject.Inject;

import fr.cea.ig.lfw.LFWConfig;

/**
 * NGL configuration typed access to NGL configuration definitions.
 * 
 * @author vrd
 *
 */
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
	public static final String[] NGL_ENV_VALUES = { "DEV", "UAT", "PROD" };
	
	/**
	 * NGL institute path in configuration.
	 */
	public static final String NGL_INSTITUTE_PATH = "institute"; 
	
	public static final String NGL_RULES_PATH = "rules.key";
	
	public static final String NGL_BARCODE_PRINTING_PATH = "ngl.printing.cb";
	
	public static final String NGL_APPLICATION_VERSION_PATH = "application.version";
	
	public static final String NGL_APPLICATION_NAME_PATH = "application.name";

	public static final String NGL_BI_URL_PATH = "bi.url"; 

	private static final String NGL_PROJECT_URL_PATH = "project.url";

	private static final String NGL_SQ_URL_PATH = "sq.url";
	
	/**
	 * Configuration to use.
	 */
	private final LFWConfig config;

	/**
	 * DI constructor.
	 * @param config config to use
	 */
	@Inject
	public NGLConfig(LFWConfig config) {
		this.config = config;
	}

	/**
	 * NGL environment, typically DEV or PROD, see NGL_ENV_VALUES. 
	 * @return NGL environment, should be the application attribute 
	 */
	// TODO: use application attribute 
	@Deprecated
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
		return config.getString(NGL_RULES_PATH);
	}
		
	/**
	 * Is the NGL bar code printing enabled ?
	 * The configuration path is {@link #NGL_BARCODE_PRINTING_PATH}. 
	 * @return false if not configured, configured value otherwise
	 */
	public boolean isBarCodePrintingEnabled() {
		return config.getBoolean(NGL_BARCODE_PRINTING_PATH, false);
	}
	
	public String getString(String path) {
		// return config.getString(path, path + ":notDefinedInConf");
		return config.getString(path);
	}
	
	public Boolean getBoolean(String path, boolean defValue) {
		return config.getBoolean(path, defValue);
	}
	
	/**
	 * Application version string if defined in the configuration file at {@link #NGL_APPLICATION_VERSION_PATH}. 
	 * @return empty string if not defined in the configuration, the configured value otherwise
	 */
	public String getApplicationVersion() {
		return config.getString(NGL_APPLICATION_VERSION_PATH,"");
	}
	
	/**
	 * Application name string if defined in the configuration file at {@link #NGL_APPLICATION_NAME_PATH}.
	 * @return empty string if not defined in the configuration, the configured value otherwise
	 */
	public String getApplicationName() {
		return config.getString(NGL_APPLICATION_NAME_PATH,"");
	}
	
	public List<String> getStringList(String path) {
		return config.getStringList(path);
	}
	
	public String getSQUrl() {
		return config.getString(NGL_SQ_URL_PATH);
	}
	
	public String getBIUrl() {
		return config.getString(NGL_BI_URL_PATH);
	}
	
	public String getProjectUrl() {
		return config.getString(NGL_PROJECT_URL_PATH);
	}
	
}
