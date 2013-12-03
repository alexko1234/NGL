package controllers.processes.api;

import controllers.ListForm;

public class ProcessesSearchForm extends ListForm{
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