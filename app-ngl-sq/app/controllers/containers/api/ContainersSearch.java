package controllers.containers.api;

public class ContainersSearch {
	public String projectCode;
	public String fromExperimentCode;
	public String stateCode;
	public String sampleCode;
	public String processTypeCode;
	@Override
	public String toString() {
		return "ContainersSearch [projectCode=" + projectCode
				+ ", fromExperimentCode=" + fromExperimentCode + ", stateCode="
				+ stateCode + ", sampleCode=" + sampleCode
				+ ", processTypeCode=" + processTypeCode + "]";
	}
	
	
}
