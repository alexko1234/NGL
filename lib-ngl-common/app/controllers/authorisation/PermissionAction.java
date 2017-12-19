package controllers.authorisation;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import play.Logger;
import play.Play;
import play.libs.F;
// import play.libs.F.Function0;
// import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;


/**
 * 
 * @author michieli
 *
 */
// TODO: cleanup
public class PermissionAction extends Action<Permission> {

	private static final String COOKIE_SESSION = "NGL_FILTER_USER";
	
	@Override
	// public F.Promise<Result> call(Context context) throws Throwable {
	public CompletionStage<Result> call(final play.mvc.Http.Context context) {
		//TODO GA need to upgrade to play 2.4 to have the benefit of :
		/*
		 Note: If you want the action composition annotation(s) put on a Controller class to be executed before the one(s) put on action methods 
		 set play.http.actionComposition.controllerAnnotationsFirst = true in application.conf. 
		 However, be aware that if you use a third party module in your project it may rely on a certain execution order of its annotations.
		 */
		String username = context.session().get(COOKIE_SESSION);
		
		String userAgent = context.request().getHeader("User-Agent");
		if (userAgent != null && userAgent.equals("bot")) {
			username = "ngsrg";
		} else if(userAgent.contains("Honeywell")) {
			username = "scanner";
		}
		
		for(String checkPermission:configuration.value()){
			if (checkPermission.equals("") || !(Play.application().configuration().getString("auth.mode").equals("prod"))) {
					return delegate.call(context);
			} else if (PermissionHelper.checkPermission(username, checkPermission)) {
						return delegate.call(context);
			} else {
				/*return Promise.promise(new Function0<Result>(){
							public Result apply() {
								return unauthorized();
							}
				});*/
				return CompletableFuture.supplyAsync(new Supplier<Result>(){
						public Result get() {
							return unauthorized();
						}
					});
			}			
		}
		return null;
	}
	
}