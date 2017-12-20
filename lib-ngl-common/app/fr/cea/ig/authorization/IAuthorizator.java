package fr.cea.ig.authorization;

import java.util.concurrent.CompletionStage;

import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

public interface IAuthorizator {

	// Caller is supposed to provide at least one permission
	public boolean authorize(String login, String[] perms);
	
}
