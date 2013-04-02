package controllers.containers.api;

public class ContainersSearch {
	public String projectCode;
	public String experimentCode;
	public String containerState;
	public String containerSample;
	public String containerProcess;
	
	public ContainersSearch(){
		this.projectCode = "";
		this.experimentCode = "";
		this.containerState = "";
		this.containerSample = "";
		this.containerProcess = "";
	}
	
	public ContainersSearch(String projectCode, String experimentCode, String containerState, String containerSample, String containerProcess) {
		this.projectCode = projectCode;
		this.experimentCode = experimentCode;
		this.containerState = containerState;
		this.containerSample = containerSample;
		this.containerProcess = containerProcess;
	}
}
