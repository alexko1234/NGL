package controllers.experiments.api;

import java.util.Date;
import java.util.List;

import controllers.ListForm;

public class ExperimentSearchForm extends ListForm{
	public String typeCode;
	public String processTypeCode;
	public String categoryCode;
	public List<String> projectCodes;
	public List<String> sampleCodes;
	public Date fromDate;
	public Date toDate;
	
	@Override
	public String toString() {
		return "ExperimentSearch [typeCode=" + typeCode + "]";
	}
}
