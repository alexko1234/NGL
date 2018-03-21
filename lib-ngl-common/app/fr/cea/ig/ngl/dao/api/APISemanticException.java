package fr.cea.ig.ngl.dao.api;

public class APISemanticException extends APIException {

	/**
	 * Use this exception if the operation called is not in adequacy with data <br>
	 * Like call create() instead of update()
	 * @param message message of exception
	 */
	public APISemanticException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
