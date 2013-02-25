package controllers.authorisation;

import java.util.ArrayList;
import java.util.Arrays;

import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

public class PermissionAction extends Action<Permission> {
	
	
	@Override
	public Result call(Context ctx) throws Throwable {
		if(PermissionHelper.checkPermission(ctx.session(), new ArrayList<String>(Arrays.asList(configuration.value())), configuration.allPermissions()) && PermissionHelper.checkTeam(ctx.session(),new ArrayList<String>(Arrays.asList(configuration.teams()))))
			return delegate.call(ctx);
		else
			return unauthorized("Acces interdit pour cette ressource");
	}
	

	
}