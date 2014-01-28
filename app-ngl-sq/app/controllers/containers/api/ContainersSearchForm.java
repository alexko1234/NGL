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
	
	@Override
	public String toString() {
		return "ContainersSearch [projectCodes=" + projectCodes.toString()
				+ ", categoryCode=" + categoryCode + ", stateCode="
				+ stateCode + ", sampleCodes=" + sampleCodes.toString() + ", experimentTypeCode=" 
				+ experimentTypeCode + ", processTypeCode=" + processTypeCode + ", supportCode="
				+ supportCode +", supportCategoryCode=" + containerSupportCategory +"]";
	}
}
