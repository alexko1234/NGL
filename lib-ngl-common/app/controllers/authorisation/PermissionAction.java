package controllers.authorisation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import play.Logger;
import play.Play;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

public class PermissionAction extends Action<Authenticate> {
	
	
	@Override
	public Result call(Context ctx) throws Throwable {
		if((configuration.value()[0].equals("") && configuration.teams()[0].equals("")) || (Play.application().configuration().getString("auth.mode") != null && !Play.application().configuration().getString("auth.mode").equals("prod"))){
			return delegate.call(ctx);
		}
		
		if(PermissionHelper.checkPermission(ctx.session(), new ArrayList<String>(Arrays.asList(configuration.value())), configuration.allPermissions()) && PermissionHelper.checkTeam(ctx.session(),new ArrayList<String>(Arrays.asList(configuration.teams()))))
			return delegate.call(ctx);
		else{
				return unauthorized("Acces interdit pour cette ressource");
		}
	}
}