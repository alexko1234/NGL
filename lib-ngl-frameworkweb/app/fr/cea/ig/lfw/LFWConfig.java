package fr.cea.ig.lfw;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.typesafe.config.Config;

/**
 * Wrapper around play configuration.
 * 
 * @author vrd
 *
 */
@Singleton
public class LFWConfig {
	
	private final Config config;
	
	@Inject
	public LFWConfig(Config config) {
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
	
	public String getCheckedString(String path, String[] values) {
		if (!config.hasPath(path))
			throw new RuntimeException(path + " has no value in configuration");
		// TODO: use some common contains method
		String value = config.getString(path);
		for (String s : values)
			if (s.equals(value))
				return value;
		throw new RuntimeException("value " + value + " at " + path + " not in allowed values:" + String.join(",", values));
	}
	
	public String getString(String path) {
		if (!config.hasPath(path))
			throw new RuntimeException(path + " has no value in configuration");
		return config.getString(path);
	}
		
	public String getString(String path, String defaultValue) {
		if (!config.hasPath(path))
			return defaultValue;
		return config.getString(path);
	}
	
	public List<String> getStringList(String path) {
		try {
			return config.getStringList(path);
		} catch (Exception e) {
			return null;
		}
	}

	
}
