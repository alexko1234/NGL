package controllers.history;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.Application.*;
/** 
 * Write user action into database
 * 
 *
 *Use with :  @With(UserHistory.class)
 *
 *This class handle the request of the user and write the action to database
 *@author ydeshayes
 */

public class UserHistory extends Action.Simple{

	@Override
	//function called by play
	public Result call(Http.Context context) throws Throwable {
		if(Play.application().configuration().getBoolean("useraction.trace") != null && Play.application().configuration().getBoolean("useraction.trace") != false){
			String params = "";
			String action = "";
			String login = "";
			
						
			if(Context.current().session().get("NGL_FILTER_USER") != null){
				login = Context.current().session().get("NGL_FILTER_USER");
			}
			
			DynamicForm data = new DynamicForm().bindFromRequest();
			params=data.data().toString();
			
			action = context.request().toString();
			Result res = null;
			
			if(context.request().uri().startsWith("/api/")){
				long start = System.currentTimeMillis();
				res = delegate.call(context);
				long timeRequest = (System.currentTimeMillis() - start);
				Logger.debug(action + " -> " + (System.currentTimeMillis() - start) + " ms.");
				
				//after request
				//ecriture de l'info
				MongoDBDAO.save("UserHistory", new UserAction(login,params,action,timeRequest));
			}else{
				res = delegate.call(context);
			}
			
			return res;
		}else{
			return delegate.call(context);
		}
	}
}