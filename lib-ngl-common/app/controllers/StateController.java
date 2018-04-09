package controllers;

import play.mvc.Result;

public interface StateController {

	/**
	 * Update the state of a resource (retrieved by its code).
	 * @param code the code of the object to update
	 * @return HTTP result
	 */
	public abstract Result updateState(String code);
	
	public abstract Result updateStateBatch();
}
