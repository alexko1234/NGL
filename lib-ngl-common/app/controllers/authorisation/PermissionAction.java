package controllers.authorisation;

import java.util.Arrays;
import play.Play;
import play.libs.F;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Result;
import play.mvc.Http.Context;

public class PermissionAction extends Action<Permission> {

	@Override
	public F.Promise<Result> call(Context ctx) throws Throwable {
		if ((configuration.value()[0].equals("") && configuration.teams()[0]
				.equals(""))
				|| (Play.application().configuration().getString("auth.mode") != null && !Play
						.application().configuration().getString("auth.mode").equals("prod"))) {
			return delegate.call(ctx);
		} else if (PermissionHelper.checkPermission(ctx.session(),
				Arrays.asList(configuration.value()),
				configuration.allPermissions())
				&& PermissionHelper.checkTeam(ctx.session(),
						Arrays.asList(configuration.teams())))
			return delegate.call(ctx);
		else {
			return Promise.promise(new Function0<Result>() {
				public Result apply() {
					return unauthorized("Acces interdit pour cette ressource");
				}
			});
		}
	}
}