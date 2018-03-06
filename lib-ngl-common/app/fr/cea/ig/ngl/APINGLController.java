package fr.cea.ig.ngl;

import play.mvc.Result;

public interface APINGLController {

	// Mandatory methods
	public abstract Result head(String code);
	public abstract Result list();
	public abstract Result get(String code);
	public abstract Result save();
	public abstract Result update(String code) ;
}
