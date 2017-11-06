package fr.cea.ig.play;

import com.typesafe.config.Config;

import javax.inject.Inject;

/**
 * Config facade defining accessors with defaults.
 * @author vrd
 *
 */
public class IGConfig {
	
	/**
	 * Underlying configuration.
	 */
	private final Config config;
	
	/**
	 * Constructor.
	 * @param config configuration to use
	 */
	@Inject
	public IGConfig(Config config) {
		this.config = config;
	}

	/**
	 * Underlying configuration instance.
	 * @return underlying configuration instance
	 */
	public Config config() { 
		return config;
	}
	
	/**
	 * Get boolean value at path in config, return the provided
	 * default value if no value is found at given path.
	 *  
	 * @param path         path to look for boolean value at
	 * @param defaultValue value to return if the path is not found in config
	 * @return             either the config value of the given defaultValue
	 */
	public boolean getBoolean(String path, boolean defaultValue) {
		if (!config.hasPath(path))
			return defaultValue;
		return config.getBoolean(path);
	}
	
}
