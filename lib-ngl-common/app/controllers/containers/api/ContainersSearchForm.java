package controllers.containers.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.Map;

import controllers.ListForm;

public class ContainersSearchForm extends ListForm{
	
	public String code; 
	public String codeRegex;
	public Set<String> codes;
	public String projectCode;
	public Set<String> projectCodes;
	public String stateCode;
	public Set<String> stateCodes;
	public String sampleCode;
	public Set<String> sampleCodes;
	public Set<String> sampleTypeCodes;
	public String categoryCode;
	public String nextExperimentTypeCode;
	public String processTypeCode;
	public String processCategory;
	public String nextProcessTypeCode;
	public String supportCode;
	public Set<String> supportCodes;
	public String supportCodeRegex;
	public String containerSupportCategory;
	public Set<String> containerSupportCategories;
	public Set<String> fromExperimentTypeCodes;
	public Set<String> valuations;
	public Date fromDate;
	public Date toDate;
	public String column; //TODO rename in supportColumn
	public String line; //TODO rename in supportLine
	public String createUser; 
	public List<String> createUsers;
	public boolean isEmptyFromExperimentTypeCodes=false;
	
	@Deprecated
	public Map<String, List<String>> properties = new HashMap<String, List<String>>();
	
	public Map<String, List<String>> processProperties = new HashMap<String, List<String>>();
	
	public Map<String, List<String>> contentsProperties = new HashMap<String, List<String>>();
	
	@Override
	public String toString() {
		return "ContainersSearchForm [projectCode=" + projectCode
				+ ", projectCodes=" + projectCodes + ", stateCode=" + stateCode
				+ ", sampleCode=" + sampleCode + ", sampleCodes=" + sampleCodes
				+ ", categoryCode=" + categoryCode + ", nextExperimentTypeCode="
				+ nextExperimentTypeCode + ", processTypeCode=" + processTypeCode
				+ ", supportCode=" + supportCode
				+ ", containerSupportCategory=" + containerSupportCategory
				+ ", containerSupportCategories=" + containerSupportCategories
				+ ", fromExperimentTypeCodes=" + fromExperimentTypeCodes
				+ ", valuations=" + valuations + ", createUser=" + createUser 
				+ ", fromDate=" + fromDate + ", toDate=" + toDate + "]";
	}
}
