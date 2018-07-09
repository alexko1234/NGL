package controllers.processes.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import controllers.ListForm;

public class ProcessesSearchForm extends ListForm {
	
	public String typeCode;
	public List<String> typeCodes;
	public String categoryCode;
	public List<String> categoryCodes;
	public String sampleCode;
	public Set<String> sampleCodes;
	public Set<String> sampleTypeCodes;
	public String projectCode;
	public Set<String> projectCodes;
	public String supportCode;
	public String supportCodeRegex;
	public Set<String> supportCodes;
	
	public String containerCode;
	public String containerCodeRegex;
	public Set<String> containerCodes;
	
	public Set<String> outputContainerCodes;
	public Set<String> inputContainerCodes;
	
	public String stateCode;
	public Set<String> stateCodes;
	public List<String> stateResolutionCodes;
	public String containerSupportCategory;
	public Date fromDate;
	public Date toDate;
	public String createUser;
	public Set<String> users;
	public String experimentCode;
	public List<String> experimentCodes;
	public String experimentCodeRegex;
	public String code;
	public List<String> codes;
	public String codeRegex;
	public Map<String, List<String>> properties = new HashMap<>();
	public String fromSupportCode;
	public Map<String, List<String>> sampleOnInputContainerProperties = new HashMap<>();
	
	@Override
	public String toString() {
		return "ProcessesSearchForm [typeCode=" + typeCode + ", categoryCode="
				+ categoryCode + ", sampleCode=" + sampleCode
				+ ", sampleCodes=" + sampleCodes + ", projectCode="
				+ projectCode + ", projectCodes=" + projectCodes
				+ ", supportCode=" + supportCode + ", stateCode=" + stateCode
				+ ", stateCodes=" + stateCodes + ", containerSupportCategory="
				+ containerSupportCategory + ", fromDate=" + fromDate
				+ ", toDate=" + toDate + ", users=" + users + ", createUser=" + createUser 
				+ ", experimentCode=" + experimentCode +", code="+ code + ", properties="
				+ properties + ", fromSupportCode="+fromSupportCode +"]";
	}
}