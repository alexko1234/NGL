package controllers.samples.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import controllers.ListForm;

public class SamplesSearchForm extends ListForm{
	public String code; 
	public String codeRegex;
	public Set<String> codes;
	public String projectCode;
	public List<String> projectCodes;
	public List<String> typeCodes;
	public Date fromDate;
	public Date toDate;
	public String createUser; 
	public List<String> createUsers;
	public String commentRegex;
	public Map<String, List<String>> properties = new HashMap<String, List<String>>();
	public Map<String, Boolean> existingFields;
	public String referenceCollabRegex;
	
	public String existingTransformationTypeCode;
	public String notExistingTransformationTypeCode;
	public String existingProcessTypeCode;
	public String notExistingProcessTypeCode;
	
	public List<String> experimentProtocolCodes;
	public String ncbiScientificNameRegex;
	public String taxonCode;
	
	public Map<String, List<String>> experimentProperties = new HashMap<String, List<String>>();
	public String toString() {
		return "SamplesSearchForm [projectCode=" + projectCode

				+ ", projectCodes=" + projectCodes + ",sampleCode=" + ", code=" + code
				+ ", typeCodes"
				+ ", createUser=" + createUser 
				+ ", fromDate=" + fromDate + ", toDate=" + toDate
				 + "]";
					}
}
