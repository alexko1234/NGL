package controllers.admin.supports.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controllers.ListForm;
import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

public class NGLObjectsSearchForm extends ListForm implements IValidation{
	
	public List<String> collectionNames;
	
	public List<String> codes;
	public String codeRegex;
	
	public String projectCode;
	public String sampleCode;
	
    public Map<String, List<String>> contentProperties = new HashMap<>();
    public String contentPropertyNameUpdated;
	
    @Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, collectionNames, "collectionNames");
		ValidationHelper.required(contextValidation, projectCode, "projectCode");
		ValidationHelper.required(contextValidation, sampleCode, "sampleCode");
		ValidationHelper.required(contextValidation, contentProperties, "contentProperties");
		ValidationHelper.required(contextValidation, contentPropertyNameUpdated, "contentPropertyNameUpdated");
		if(!contextValidation.hasErrors()){
			ValidationHelper.required(contextValidation, contentProperties.get(contentPropertyNameUpdated), "contentProperties."+contentPropertyNameUpdated);
		}
		
	}
    
     
}
