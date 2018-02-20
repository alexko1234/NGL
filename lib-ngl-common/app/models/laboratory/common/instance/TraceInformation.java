package models.laboratory.common.instance;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

/**
 * TraceInformation are embedded in objects that trace unix like information
 * about creation and modification.
 * 
 * @author mhaquell
 * @author vrd
 */
public class TraceInformation implements IValidation {
	
	/**
	 * Creation user name, user that created the embedding object.
	 * Misnamed, should be creationUser or creationUserName. 
	 */
	public String createUser;
	
	/**
	 * Creation date, date at which the embedding object was created.
	 */
	public Date creationDate;	
	
	/**
	 * Modification user name, last user that modified the embedding object.
	 * Misnamed, should be modificationUser or modificationUserName.
	 */
	public String modifyUser;
	
	/**
	 * Modification date, last date of embedding object modification.
	 * Misnamed, should be modificationDate. 
	 */
	public Date modifyDate;

	/**
	 * No field is set in the object.
	 */
	public TraceInformation() {
	}
	
	/**
	 * Constructs the trace with the given user as creation user. 
	 * @param createUser user name 
	 */
	public TraceInformation(String createUser) {
		if (createUser != null) {
			this.createUser   = createUser;
			this.creationDate = new Date();
		}
	}

	/**
	 * @deprecated bogus behavior when the creation user has not been properly set, replaced by {@link #creationStamp(ContextValidation,String)} {@link #modificationStamp(ContextValidation,String)}
	 * @param user user name
	 */
	// @Deprecated 
	@JsonIgnore
	public void setTraceInformation(String user) {
		if (createUser == null) {
			createUser   = user;
			creationDate = new Date();
		} else {
			modifyUser = user;
			modifyDate = new Date();
		}				
	}

	/**
	 * Set the creation information using the provided user name and the
	 * current date.
	 * @param ctx  validation context
	 * @param user user name for the creation
	 */
	public void creationStamp(ContextValidation ctx, String user) {
		Date date = new Date();
		if (createUser != null) {
			ctx.addError("TraceInformation.setCreationStamp","the creation stamp already exists");
		} else {
			forceCreationStamp(user,date);
		}
	}
	
	/**
	 * Force this trace creation information.
	 * @param user creation user
	 * @param date creation date
	 */
	public void forceCreationStamp(String user, Date date) {
		createUser   = user;
		creationDate = date;			
	}
	
	/**
	 * Force this trace creation information using the current date.
	 * @param user creation user
	 */
	public void forceCreationStamp(String user) {
		forceCreationStamp(user, new Date());
	}
	
	/**
	 * Set the update information using the the provided user and the current date.
	 * @param ctx  validation context
	 * @param user user name for the update
	 */
	public void modificationStamp(ContextValidation ctx, String user) {
		Date date = new Date();
		forceModificationStamp(user,date);
	}
	
	/**
	 * Force this trace modification information. 
	 * @param user modification user
	 * @param date modification date
	 */
	public void forceModificationStamp(String user, Date date) {
		modifyUser = user;
		modifyDate = date;			
	}
	
	/**
	 * Force this trace modification information using the current date.
	 * @param user modification user.
	 */
	public void forceModificationStamp(String user) {
		forceModificationStamp(user, new Date());
	}
	
	@Override
	public void validate(ContextValidation contextValidation) {
		//backward compatibility
		if (contextValidation.isUpdateMode() || (contextValidation.isNotDefined() && contextValidation.getObject("_id") != null)) {
			ValidationHelper.required(contextValidation, createUser,   "createUser");
			ValidationHelper.required(contextValidation, creationDate, "creationDate");
			ValidationHelper.required(contextValidation, modifyUser,   "modifyUser");
			ValidationHelper.required(contextValidation, modifyDate,   "modifyDate");
		} else {
			ValidationHelper.required(contextValidation, createUser,   "createUser");
			ValidationHelper.required(contextValidation, creationDate, "createDate");
		}
	}
	
}
