package controllers.containers.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controllers.ListForm;

public class ContainerSupportsSearchForm extends ListForm {
	public String code;
	public String codeRegex;
	public String containerSupportCategory;
	public String categoryCode;
	public String stateCode;
	public List<String> stateCodes;
	public String nextExperimentTypeCode;
	public String processTypeCode;
	public List<String> projectCodes;
	public List<String> sampleCodes;
	public List<String> fromExperimentTypeCodes;
	
	public List<String> valuations;
	public Date fromDate;
	public Date toDate;
	public List<String> users;
	public Map<String, List<String>> properties = new HashMap<String, List<String>>();
	
	@Override
	public String toString() {
		return "SupportsSearchForm [code=" + code + ", categoryCode="
				+ categoryCode + ", stateCode=" + stateCode
				+ ", nextExperimentTypeCode=" + nextExperimentTypeCode
				+ ", processTypeCode=" + processTypeCode + ", projectCodes="
				+ projectCodes + ", sampleCodes=" + sampleCodes
				+ ", containerSupportCategory=" + containerSupportCategory
				+ ", fromExperimentTypeCodes=" + fromExperimentTypeCodes
				+ ", fromDate=" + fromDate + ", toDate=" + toDate + ", users="
				+ users + "]";
	}
}
