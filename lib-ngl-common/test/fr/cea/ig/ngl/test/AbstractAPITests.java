package fr.cea.ig.ngl.test;

import org.junit.Assert;
import org.junit.BeforeClass;

import fr.cea.ig.ngl.dao.api.APIValidationException;
import play.Logger;
import play.Logger.ALogger;
import utils.AbstractTests;


/**
 * normalization of DAO/API tests
 * @author ajosso
 *
 */
public interface AbstractAPITests {
	
	public abstract ALogger logger();
	
	/**
	 * 
	 * @param message
	 */
	default void exit(String message) {
		deleteData();
		Assert.fail(message);
	}

	/**
	 * Log Validation Errors only if logger level is DEBUG or less
	 * @param e
	 */
	default void logValidationErrors(APIValidationException e) {
		if(logger().isDebugEnabled()) {
			e.getErrors().keySet().forEach(key -> {
				e.getErrors().get(key).forEach(err -> {
					logger().error(key + " - "+ err.message());						
				});

			});
		}
	}
	
	// Mandatory methods
	/**
	 * Create required Data for test
	 */
	public abstract void setUpData();
	
	/**
	 * Delete Data used in test
	 */
	public abstract void deleteData();
	
}
