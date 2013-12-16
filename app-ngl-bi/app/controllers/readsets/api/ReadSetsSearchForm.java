package controllers.readsets.api;

import java.util.Date;
import java.util.List;

import play.data.format.Formats.DateTime;

import controllers.ListForm;



public class ReadSetsSearchForm extends ListForm{
	
	public List<String> stateCodes;
	public String stateCode;
	
	public List<String> projectCodes;
	public String projectCode;
	
	public List<String> sampleCodes;
	public String sampleCode;
	
	public List<String> runCodes;
	public String runCode;
	
	public List<Integer> laneNumbers;
	public Integer laneNumber;
	
	
	public String bioinformaticValidCode;
	public String productionValidCode;
	
	public List<String> runTypeCodes;
	
	public Date fromDate;
	public Date toDate;
	
	
	 
}
