package controllers.containers.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controllers.ListForm;

public class ContainersSearchForm extends ListForm{
	
	public String code;
	public String projectCode;
	public List<String> projectCodes;
	public String stateCode;
	public List<String> stateCodes;
	public String sampleCode;
	public List<String> sampleCodes;
	public String categoryCode;
	public String nextExperimentTypeCode;
	public String processTypeCode;
	public String nextProcessTypeCode;
	public String supportCode;
	public String containerSupportCategory;
	public List<String> containerSupportCategories;
	public List<String> fromExperimentTypeCodes;
	public List<String> valuations;
	public Date fromDate;
	public Date toDate;
	public String column;
	public String line;
	public String createUser;
	public boolean isEmptyFromExperimentTypeCodes=true;
	public Map<String, List<String>> properties = new HashMap<String, List<String>>();
	
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
