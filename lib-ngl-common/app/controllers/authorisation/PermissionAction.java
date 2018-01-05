package controllers.authorisation;

import fr.cea.ig.authentication.Authentication;
import fr.cea.ig.authorization.IAuthorizator;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import javax.inject.Inject;

import play.Logger;
import play.Play;
import play.libs.F;
// import play.libs.F.Function0;
// import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

/**
 * Implements action for the permission annotation.
 * 
 * @author michieli
 * @author vrd
 * 
 */
public class PermissionAction extends Action<Permission> {

	/**
	 * Authorizator delegate.
	 */
	private final IAuthorizator authorizator;
	
	@Inject
	public PermissionAction(IAuthorizator authorizator) {
		this.authorizator = authorizator;
	}
	
	@Override
	public CompletionStage<Result> call(final play.mvc.Http.Context context) {
		if (configuration.value().length == 0) 
			throw new RuntimeException("badly configured permission control with no values");
		if (!Authentication.isAuthenticatedSession(context.session()))
			return CompletableFuture.supplyAsync(() -> unauthorized("not authenticated"));
		String username = Authentication.getUser(context.session());
		// We run the authorizator implementation.
		if (authorizator.authorize(username, configuration.value()))
			return delegate.call(context);
		else
			return CompletableFuture.supplyAsync(() -> unauthorized());
	}
	
}



