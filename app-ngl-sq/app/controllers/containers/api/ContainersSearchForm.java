package controllers.containers.api;

import controllers.ListForm;

public class ContainersSearchForm extends ListForm{
	public String projectCode;
	public String stateCode;
	public String sampleCode;
	public String categoryCode;
	public String experimentTypeCode;
	public String processTypeCode;
	public String supportCode;
	public String containerSupportCategory;
	
	@Override
	public String toString() {
		return "ContainersSearch [projectCode=" + projectCode
				+ ", categoryCode=" + categoryCode + ", stateCode="
				+ stateCode + ", sampleCode=" + sampleCode + ", experimentTypeCode=" 
				+ experimentTypeCode + ", processTypeCode=" + processTypeCode + ", supportCode="
				+ supportCode +", supportCategoryCode=" + containerSupportCategory +"]";
	}
}
