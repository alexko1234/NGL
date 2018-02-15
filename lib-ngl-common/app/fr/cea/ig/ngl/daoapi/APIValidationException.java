package fr.cea.ig.ngl.daoapi;

import validation.ContextValidation;

public class APIValidationException extends APIException {
	
	/**
	 * Eclipse requested.
	 */
	private static final long serialVersionUID = 1L;

	private final ContextValidation ctx;
	
	// Should use the validation error data.
	public APIValidationException(ContextValidation ctx) {
		super("validation error");
		this.ctx = ctx;
	}
	
	public ContextValidation getValidationContext() {
		return ctx;
	}

}
