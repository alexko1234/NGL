package controllers.admin.supports.api;

import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;
import fr.cea.ig.DBObject;

public class NGLObject extends DBObject implements IValidation{
	
		public String code;
		public String typeCode;		
		public String collectionName;
		
		
		public String contentPropertyNameUpdated;
		public String projectCode;
		public String sampleCode;
		public String currentValue;
		public String newValue;
		
		@Override
		public void validate(ContextValidation contextValidation) {
			ValidationHelper.required(contextValidation, code, "code");
			ValidationHelper.required(contextValidation, collectionName, "collectionName");
			ValidationHelper.required(contextValidation, contentPropertyNameUpdated, "contentPropertyNameUpdated");
			ValidationHelper.required(contextValidation, projectCode, "projectCode");
			ValidationHelper.required(contextValidation, sampleCode, "sampleCode");
			ValidationHelper.required(contextValidation, currentValue, "currentValue");
			ValidationHelper.required(contextValidation, newValue, "newValue");
			
		}
		
}
