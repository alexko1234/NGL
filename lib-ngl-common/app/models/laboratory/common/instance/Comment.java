package models.laboratory.common.instance;

import java.util.Date;

import models.utils.HelperObjects;

import org.codehaus.jackson.annotate.JsonIgnore;

import controllers.administration.authentication.User;

/**
 * Comment are embedded data in collection like Container, Experiment.... 
 * 
 * @author mhaquell
 *
 */
public class Comment {

	
	public String comment;
	public String createUser;
	public Date creationDate;
	
	public Comment(String comment) {
		setComment(comment);
	}
	
	public Comment(){
		
	}
	
	public void setComment(String comment){
		
		if(creationDate==null) 
			creationDate = new Date();
		this.comment=comment;
		
	}

	@JsonIgnore
	public User getCreateUser(){
		return new HelperObjects<User>().getObject(User.class, createUser);
	}
	
}
