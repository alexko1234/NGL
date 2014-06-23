package controllers.experiments.api;

import java.util.Date;
import java.util.List;

import controllers.ListForm;

public class ExperimentSearchForm extends ListForm{
	public String typeCode;
	public String processTypeCode;
	public String categoryCode;
	public String projectCode;
	public List<String> projectCodes;
	public List<String> sampleCodes;
	public String sampleCode;
	public Date fromDate;
	public Date toDate;
	public String stateCode;
	public List<String> users;
	
	@Override
	public String toString() {
		return "ExperimentSearchForm [typeCode=" + typeCode
				+ ", processTypeCode=" + processTypeCode + ", categoryCode="
				+ categoryCode + ", projectCodes=" + projectCodes
				+ ", sampleCodes=" + sampleCodes + ", fromDate=" + fromDate
				+ ", toDate=" + toDate + ", stateCode=" + stateCode
				+ ", users=" + users + "]";
	}
}
