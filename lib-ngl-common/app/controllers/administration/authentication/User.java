package controllers.administration.authentication;
/**
 * Manage the technicals users authentification and the user logout
 * 
 * @author ydeshayes
 */
import controllers.CommonController;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class User extends CommonController{

		public static Result logOut(){
			Http.Context.current().session().remove("NGL_FILTER_USER");
			Http.Context.current().session().remove("NGL_FILTER_TIMEOUT");
			return ok();//portail d'application
		}
}
