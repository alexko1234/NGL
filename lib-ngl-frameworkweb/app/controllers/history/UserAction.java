package controllers.history;


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
	public String action;
	public String params;
	public Date date;
	public long timeRequest;
	
	
	
	//default constructor for mongodb
	public UserAction(){
		this.login = "";
		this.params = "";
		this.action = "";
		this.timeRequest = 0;
	    this.date = new Date();
	}
	
	public UserAction(String varLogin,String varParams, String varAction, long varTimeRequest){
		this.login = varLogin;
		this.params = varParams;
		this.action = varAction;
	    this.timeRequest = varTimeRequest;
	    this.date = new Date();
	}
}
