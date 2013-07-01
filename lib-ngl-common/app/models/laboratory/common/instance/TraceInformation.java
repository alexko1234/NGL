package models.laboratory.common.instance;

import java.util.Date;

import models.utils.HelperObjects;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;

import play.Logger;

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
	
}
