package controllers.containers.api;

import java.util.List;

import controllers.ListForm;

public class ContainersSearchForm extends ListForm{
	public String projectCode;
	public List<String> projectCodes;
	public String stateCode;
	public String sampleCode;
	public List<String> sampleCodes;
	public String categoryCode;
	public String experimentTypeCode;
	public String processTypeCode;
	public String supportCode;
	public String containerSupportCategory;
	public List<String> fromExperimentTypeCodes;
	
	@Override
	public String toString() {
		return "ContainersSearchForm [projectCode=" + projectCode
				+ ", projectCodes=" + projectCodes + ", stateCode=" + stateCode
				+ ", sampleCode=" + sampleCode + ", sampleCodes=" + sampleCodes
				+ ", categoryCode=" + categoryCode + ", experimentTypeCode="
				+ experimentTypeCode + ", processTypeCode=" + processTypeCode
				+ ", supportCode=" + supportCode
				+ ", containerSupportCategory=" + containerSupportCategory
				+ ", fromExperimentTypeCodes=" + fromExperimentTypeCodes + "]";
	}
}
