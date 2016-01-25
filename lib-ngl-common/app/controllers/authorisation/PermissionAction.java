package controllers.authorisation;

import play.Logger;
import play.Play;
import play.libs.F;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

public class PermissionAction extends Action<Permission> {

	@Override
	public F.Promise<Result> call(Context ctx) throws Throwable {
		if ((configuration.value()[0].equals("") // && configuration.teams()[0].equals("")
				) 	|| (Play.application().configuration().getString("auth.mode") != null 
					&& !Play.application().configuration().getString("auth.mode").equals("prod"))) {
			return delegate.call(ctx);
		} else if (PermissionHelper.checkPermission(ctx.session(), configuration.value()[0])
				// && PermissionHelper.checkTeam(ctx.session(),Arrays.asList(configuration.teams()))
				){
			Logger.debug("Valeur de checkPermission: " + PermissionHelper.checkPermission(ctx.session(), configuration.value()[0]));
			return delegate.call(ctx);
		} else {
			Logger.debug("Valeur de checkPermission: " + PermissionHelper.checkPermission(ctx.session(), configuration.value()[0]));
			return Promise.promise(new Function0<Result>() {
				public Result apply() {
					return unauthorized("Vous n'avez pas les permissions nécessaires !");
				}
			});
		}
	}
}