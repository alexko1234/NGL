package controllers;

import play.mvc.Result;

public interface StateController {

	public abstract Result updateState();
	public abstract Result updateStateBatch();
}
