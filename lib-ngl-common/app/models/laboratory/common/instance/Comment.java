package models.laboratory.common.instance;

import java.util.Date;

import models.utils.CodeHelper;


import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

/**
 * Comment are embedded data in collection like Container, Experiment.... 
 * 
 *
 */
public class Comment implements IValidation {

	public String code;
	public String comment;
	public String createUser;
	public Date creationDate;
	
	public Comment(String comment, String user) {
		this.createUser = user;
		this.comment=comment;
		this.creationDate = new Date();
		this.code = CodeHelper.getInstance().generateExperimentCommentCode(this);		
	}
	
	public Comment(){
		
	}
	

	@Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, code, "code"); //TODO check if not exist on the same object
		ValidationHelper.required(contextValidation, comment, "comment");
		ValidationHelper.required(contextValidation, createUser, "createUser");
		ValidationHelper.required(contextValidation, creationDate, "creationDate");
	}
	
}
