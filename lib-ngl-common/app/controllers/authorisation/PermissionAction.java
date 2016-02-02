package controllers.authorisation;

import play.Logger;
import play.Play;
import play.libs.F;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
/**
 * 
 * @author michieli
 *
 */
public class PermissionAction extends Action<Permission> {

	@Override
	public F.Promise<Result> call(Context ctx) throws Throwable {
		for(String checkPermission:configuration.value()){
			if(checkPermission.equals("") || !(Play.application().configuration().getString("auth.mode").equals("prod"))){
					return delegate.call(ctx);
			} else if (PermissionHelper.checkPermission(ctx.session(), checkPermission)){
						return delegate.call(ctx);
			} else {
				return Promise.promise(new Function0<Result>(){
							public Result apply() {
								return unauthorized();
							}
						});
			}			
		}
		return null;
	}
}