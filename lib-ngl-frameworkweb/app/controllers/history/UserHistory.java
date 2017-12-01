package controllers.history;

import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Http.RequestBody;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

import fr.cea.ig.MongoDBDAO;
import play.mvc.Result;
import play.libs.F;
//import play.libs.F.Function0;
//import play.libs.F.Promise;

/** 
 * Write user action into database
 * 
 *
 *Use with :  @With(UserHistory.class)
 *
 *This class handle the request of the user and write the action to database
 *@author ydeshayes
 */

// TODO: clean 
public class UserHistory extends Action.Simple {

	@Override
	//function called by play
	// public  F.Promise<Result> call(Http.Context context) throws Throwable {
	public CompletionStage<Result> call(final play.mvc.Http.Context context) {
		if (Play.application().configuration().getBoolean("useraction.trace") != null 
			&& Play.application().configuration().getBoolean("useraction.trace") != false){
			
			// F.Promise<Result> res = null;
			CompletionStage<Result> res = null;
			
			if (context.request().uri().startsWith("/api/") && !context.request().uri().contains("/authentication")) {
				// String login = context.request().username();
				// String login = fr.cea.ig.authentication.Helper.username(context.request());
				String login = fr.cea.ig.authentication.Helper.username(context.session());
				String params = Json.toJson(context.request().queryString()).toString();
				String action = context.request().toString();
				
				String body = "{}";
				RequestBody rb = context.request().body();
				if(rb != null && rb.asJson() != null){
					body = rb.asJson().toString();
				}
				
				
				long start = System.currentTimeMillis();
				res = delegate.call(context);
				long timeRequest = (System.currentTimeMillis() - start);
				Logger.debug("("+login+") - " +action+ " -> " + (System.currentTimeMillis() - start) + " ms.");
				
				//after request
				//ecriture de l'info
				MongoDBDAO.save("UserHistory", new UserAction(login,params,body,action,timeRequest));
			} else {
				res = delegate.call(context);
			}
			return res;
		} else {
			return delegate.call(context);
		}
	}
}


