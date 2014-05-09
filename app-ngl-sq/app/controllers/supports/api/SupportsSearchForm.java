package controllers.supports.api;

import java.util.Date;
import java.util.List;

import controllers.ListForm;

public class SupportsSearchForm extends ListForm {
	public String code;
	public String categoryCode;
	public String stateCode;
	public String experimentTypeCode;
	public String processTypeCode;
	public List<String> projectCodes;
	public List<String> sampleCodes;
	public List<String> fromExperimentTypeCodes;
	
	public Date fromDate;
	public Date toDate;
	public List<String> users;
	
	@Override
	public String toString() {
		return "SupportsSearchForm [code=" + code + ", categoryCode="
				+ categoryCode + ", stateCode=" + stateCode
				+ ", experimentTypeCode=" + experimentTypeCode
				+ ", processTypeCode=" + processTypeCode + ", projectCodes="
				+ projectCodes + ", sampleCodes=" + sampleCodes
				+ ", fromExperimentTypeCodes=" + fromExperimentTypeCodes
				+ ", fromDate=" + fromDate + ", toDate=" + toDate + ", users="
				+ users + "]";
	}
}
