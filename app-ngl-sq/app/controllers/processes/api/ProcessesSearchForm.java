package controllers.processes.api;

import java.util.Date;
import java.util.List;

import controllers.ListForm;

public class ProcessesSearchForm extends ListForm{
	public String typeCode;
	public String categoryCode;
	public String sampleCode;
	public String projectCode;
	public String supportCode;
	public String stateCode;
	public String containerSupportCategory;
	public Date fromDate;
	public Date toDate;
	public List<String> users;
	
	@Override
	public String toString() {
		return "ProcessesSearchForm [typeCode=" + typeCode + ", categoryCode="
				+ categoryCode + ", sampleCode=" + sampleCode
				+ ", projectCode=" + projectCode + ",supportCode="+supportCode 
				+ ", stateCode="+stateCode+", containerSupportCategory="+containerSupportCategory
				+"]";
	}
}