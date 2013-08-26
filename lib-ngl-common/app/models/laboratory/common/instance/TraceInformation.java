package models.laboratory.common.instance;

import java.util.Date;

import models.utils.HelperObjects;
import models.utils.IValidation;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;

import play.Logger;
import validation.utils.ContextValidation;
import validation.utils.ValidationHelper;
import controllers.administration.authentication.User;

/**
 * 
 * TraceInformation are embedded data in collection like Experiment, Container...
 * 
 * @author mhaquell
 *
 */
public class TraceInformation implements IValidation {
	
	
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

	@Override
	public void validate(ContextValidation contextValidation) {
		if (contextValidation.getObject("_id") != null) {
			if(ValidationHelper.required(contextValidation, this, "traceInformation")){
				ValidationHelper.required(contextValidation, createUser, "traceInformation.createUser");
				ValidationHelper.required(contextValidation, creationDate, "traceInformation.creationDate");
				ValidationHelper.required(contextValidation, modifyUser, "traceInformation.modifyUser");
				ValidationHelper.required(contextValidation, modifyDate, "traceInformation.modifyDate");
			}
		} else {
			if(ValidationHelper.required(contextValidation, this, "traceInformation")){
				ValidationHelper.required(contextValidation, createUser, "traceInformation.modifyUser");
				ValidationHelper.required(contextValidation, creationDate, "traceInformation.modifyDate");
			}
		}
	}
	
}
