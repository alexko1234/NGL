package fr.cea.ig.ngl.dao.api;

public class APIValidationException extends APIException {
	
	/**
	 * Eclipse requested.
	 */
	private static final long serialVersionUID = 1L;

	// Should use the validation error data.
	public APIValidationException(String message) {
		super(message);
	}

}
