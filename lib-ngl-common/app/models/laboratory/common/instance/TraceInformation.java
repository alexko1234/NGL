package models.laboratory.common.instance;

import java.util.Date;

import models.utils.HelperObjects;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;

import controllers.administration.authentication.User;

/**
 * 
 * TraceInformation are embedded data in collection like Experiment, Container...
 * 
 * @author mhaquell
 *
 */
public class TraceInformation {
	
	
	public String createUser;
	public Date creationDate;	
	
	public String modifyUser;
	public Date modifyDate;
	
	@JsonCreator
	public TraceInformation(){
		
	}
	
	public void setTraceInformation(String user){
		
		if (createUser==null){
			createUser=user;
		} else {
			modifyUser=user;
		}
		
		if(creationDate==null) {
			creationDate = new Date();
		} else { modifyDate=new Date(); }

		
	}
	
	@JsonIgnore
	public User setCreateUser(){
		return new HelperObjects<User>().getObject(User.class, createUser, null);
	}
	
	
	@JsonIgnore
	public User setModifyUser(){
		return new HelperObjects<User>().getObject(User.class, modifyUser, null);
	}
}
