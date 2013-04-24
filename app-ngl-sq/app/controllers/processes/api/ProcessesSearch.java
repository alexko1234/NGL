package controllers.processes.api;

public class ProcessesSearch {
	public String typeCode;
	public String sampleCode;
	public String projectCode;
	@Override
	public String toString() {
		return "ProcessesSearch [projectCode=" + projectCode
				+ ", sampleCode=" + sampleCode
				+ ", typeCode=" + typeCode + "]";
	}
}