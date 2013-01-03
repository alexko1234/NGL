package models.history;


/**
 * User action model
 * 
 * This is the model stored in mongodb
 * 
 * @author ydeshayes
 */

import java.util.Date;
import fr.cea.ig.DBObject;

public class UserAction extends DBObject{
	
	public String login;
	public String params;
	public String action;
	public Date date;
	
	
	//default constructor for mongodb
	public UserAction(){
		this.login = "";
		this.params = "";
		this.action = "";
	    this.date = new Date();
	}
	
	public UserAction(String varLogin,String varParams, String varAction){
		this.login = varLogin;
		this.params = varParams;
		this.action = varAction;
	    this.date = new Date();
	}
}
