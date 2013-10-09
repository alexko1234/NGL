package models.laboratory.common.instance;

import java.util.Date;
import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

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
			creationDate = new Date();
		} else {
			modifyUser=user;
			modifyDate=new Date();
		}				
	}

	@Override
	public void validate(ContextValidation contextValidation) {
		//backward compatibility
		if (contextValidation.isUpdateMode() || (contextValidation.isNotDefined() && contextValidation.getObject("_id") != null)) {
			ValidationHelper.required(contextValidation, createUser, "createUser");
			ValidationHelper.required(contextValidation, creationDate, "creationDate");
			ValidationHelper.required(contextValidation, modifyUser, "modifyUser");
			ValidationHelper.required(contextValidation, modifyDate, "modifyDate");
		} else {
			ValidationHelper.required(contextValidation, createUser, "createUser");
			ValidationHelper.required(contextValidation, creationDate, "createDate");
		}
	}
	
}
