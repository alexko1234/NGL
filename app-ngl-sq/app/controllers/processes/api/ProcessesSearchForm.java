package controllers.processes.api;

import controllers.ListForm;

public class ProcessesSearchForm extends ListForm{
	public String typeCode;
	public String categoryCode;
	public String sampleCode;
	public String projectCode;
	
	@Override
	public String toString() {
		return "ProcessesSearchForm [typeCode=" + typeCode + ", categoryCode="
				+ categoryCode + ", sampleCode=" + sampleCode
				+ ", projectCode=" + projectCode + "]";
	}
}