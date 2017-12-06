package models.laboratory.common.instance;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationHelper;

// This link : {@link models.laboratory.common.instance.State}

/**
 * State, at least embedded in 
 * <ul>
 *   <li>{@link models.laboratory.container.instance.Container}.</li>
 *   <li>{@link models.laboratory.container.instance.ContainerSupport}</li>
 * </ul>
 * 
 * @author vrd
 *
 */
public class State implements IValidation {

	/**
	 * State code, possible values are defined in {@link models.laboratory.common.description.State}.
	 */
	public String code;
	
	// access or creation date
	public Date date;
	public String user;
	
	public Set<String> resolutionCodes;

	public Set<TransientState> historical;

	public State(String code, String user) {
		this.code = code;
		this.user = user;
		date      = new Date();
	}
	
	public State() {
		date = new Date();
	}
	
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		CommonValidationHelper.validateStateCode(code, contextValidation);
		ValidationHelper.required(contextValidation, date, "date");
		ValidationHelper.required(contextValidation, user, "user");
		CommonValidationHelper.validateResolutionCodes(resolutionCodes,	contextValidation);
	}

}
