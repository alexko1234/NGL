package controllers.containers.api;

public class ContainersSearchForm {
	public String projectCode;
	public String stateCode;
	public String sampleCode;
	public String categoryCode;
	@Override
	public String toString() {
		return "ContainersSearch [projectCode=" + projectCode
				+ ", categoryCode=" + categoryCode + ", stateCode="
				+ stateCode + ", sampleCode=" + sampleCode + "]";
	}
}
