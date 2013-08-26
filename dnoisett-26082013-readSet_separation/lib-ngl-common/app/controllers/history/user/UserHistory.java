package controllers.history.user;
import fr.cea.ig.MongoDBDAO;
import play.data.DynamicForm;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;
/** 
 * Write user action into database
 * 
 *
 *Use with :  @With(UserHistory.class)
 *
 *This class handle the request of the user and write the action to database
 *@author ydeshayes
 */
import models.history.UserAction;

public class UserHistory extends Action.Simple{

	@Override
	//function called by play
	public Result call(Http.Context context) throws Throwable {
		String params = "";
		String action = "";
		String login = "";
		Context.current.set(context);

		login = Context.current().session().get("CAS_FILTER_USER");
		
		DynamicForm data = new DynamicForm().bindFromRequest();
		params=data.data().toString();
		
		action = context.request().toString();
		
		Result res = delegate.call(context);
		
		//after request
		if(res.toString().indexOf("SimpleResult(200,") != -1){
			//ecriture de l'info
			MongoDBDAO.save("userHistory", new UserAction(login,params,action));
		}
		
		return res;
	}
}