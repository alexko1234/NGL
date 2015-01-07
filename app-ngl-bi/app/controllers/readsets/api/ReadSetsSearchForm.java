package controllers.readsets.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public String regexCode;
	public String regexSampleCode;
    
    public List<String> instrumentCodes;	
    public List<String> productionResolutionCodes;
    public List<String> bioinformaticResolutionCodes;
    
    public String productionValuationUser;
    
    public List<String> existingFields, notExistingFields;
    
    public String isSentCCRT, isSentCollaborator;

    public Map<String, List<String>> properties = new HashMap<String, List<String>>();
    public Map<String, List<String>> sampleOnContainerProperties = new HashMap<String, List<String>>();
    public Map<String, Map<String, List<String>>> treatmentProperties = new HashMap<String, Map<String, List<String>>>();
    
}
