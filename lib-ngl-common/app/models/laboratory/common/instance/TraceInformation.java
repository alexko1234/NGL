package models.laboratory.common.instance;

import java.util.Date;

import models.utils.HelperObjects;
import models.utils.IValidation;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;

import play.Logger;
import validation.utils.ContextValidation;
import validation.utils.ConstraintsHelper;
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
		if (contextValidation.contextObjects.get("_id") != null) {
			if(ConstraintsHelper.required(contextValidation.errors, this, "traceInformation")){
				ConstraintsHelper.required(contextValidation.errors, createUser, "traceInformation.createUser");
				ConstraintsHelper.required(contextValidation.errors, creationDate, "traceInformation.creationDate");
				ConstraintsHelper.required(contextValidation.errors, modifyUser, "traceInformation.modifyUser");
				ConstraintsHelper.required(contextValidation.errors, modifyDate, "traceInformation.modifyDate");
			}
		} else {
			if(ConstraintsHelper.required(contextValidation.errors, this, "traceInformation")){
				ConstraintsHelper.required(contextValidation.errors, createUser, "traceInformation.createUser");
				ConstraintsHelper.required(contextValidation.errors, creationDate, "traceInformation.creationDate");
			}
		}
	}
	
}
