package controllers.admin.supports.api;

import fr.cea.ig.DBObject;
import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

public class NGLObject extends DBObject implements IValidation{

	public enum Action { delete, replace, exchange }

//	public String code;
	public String typeCode;		
	public String collectionName;

	public String contentPropertyNameUpdated;
	public String projectCode;
	public String sampleCode;
	public String currentValue;
	public String newValue;
	public String readSetToSwitchCode;

	public String action;
	public Long nbOccurrences;

	@Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, code, "code");
		ValidationHelper.required(contextValidation, collectionName, "collectionName");
		ValidationHelper.required(contextValidation, contentPropertyNameUpdated, "contentPropertyNameUpdated");
		ValidationHelper.required(contextValidation, projectCode, "projectCode");
		ValidationHelper.required(contextValidation, sampleCode, "sampleCode");
		ValidationHelper.required(contextValidation, currentValue, "currentValue");

		if (ValidationHelper.required(contextValidation, action, "action") 
				&& Action.replace.toString().equals(action)){
			ValidationHelper.required(contextValidation, newValue, "newValue");
		}
	}

}
