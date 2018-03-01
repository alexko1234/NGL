package fr.cea.ig.ngl.dao.api;

import java.util.List;
import java.util.Map;

import play.data.validation.ValidationError;

public class APIValidationException extends APIException {
	
	/**
	 * Eclipse requested.
	 */
	private static final long serialVersionUID = 1L;
	
	private final Map<String, List<ValidationError>> errors;

	public APIValidationException(String message) {
		super(message);
		this.errors = null;
	}
	
	public APIValidationException(String message, Throwable t) {
		super(message, t);
		this.errors = null;
	}
	
	public APIValidationException(String message, Map<String, List<ValidationError>> errors) {
		super(message);
		this.errors = errors;
	}
	
	public APIValidationException(String message, Throwable t, Map<String, List<ValidationError>> errors) {
		super(message, t);
		this.errors = errors;
	}

	public Map<String, List<ValidationError>> getErrors() {
		return errors;
	}

}
