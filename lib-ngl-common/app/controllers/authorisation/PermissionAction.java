package controllers.authorisation;

import java.util.Arrays;
import play.Play;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

public class PermissionAction extends Action<Permission> {
	
	
	@Override
	public Result call(Context ctx) throws Throwable {
		if((configuration.value()[0].equals("") && configuration.teams()[0].equals("")) 
				|| (Play.application().configuration().getString("auth.mode") != null 
				&& !Play.application().configuration().getString("auth.mode").equals("prod"))){
			return delegate.call(ctx);
		}else if(PermissionHelper.checkPermission(ctx.session(), Arrays.asList(configuration.value()), configuration.allPermissions()) 
				&& PermissionHelper.checkTeam(ctx.session(),Arrays.asList(configuration.teams())))
			return delegate.call(ctx);
		else{
			return unauthorized("your are not authorized to use this resource");
		}
	}
}