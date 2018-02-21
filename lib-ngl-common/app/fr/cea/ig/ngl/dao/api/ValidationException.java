package fr.cea.ig.ngl.dao.api;

import java.util.List;
import java.util.Map;

import play.data.validation.ValidationError;

public class ValidationException extends APIException {

	private final Map<String, List<ValidationError>> errors;
	
	public ValidationException(String message) {
		super(message);
		this.errors = null;
	}
	
	public ValidationException(String message, Throwable t) {
		super(message, t);
		this.errors = null;
	}
	
	public ValidationException(String message, Map<String, List<ValidationError>> errors) {
		super(message);
		this.errors = errors;
	}

	public Map<String, List<ValidationError>> getErrors() {
		return errors;
	}

}
