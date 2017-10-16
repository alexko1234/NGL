package controllers.readsets.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import play.data.format.Formats.DateTime;
import controllers.ListForm;



public class ReadSetsSearchForm extends ListForm{
	
	public String typeCode;
	public Set<String> typeCodes;
	
	public Set<String> stateCodes;
	public String stateCode;
	
	public Set<String> submissionStateCodes;
	public String submissionStateCode;
	
	
	public Set<String> projectCodes;
	public String projectCode;
	
	public Set<String> sampleCodes;
	public String sampleCode;
	
	public Set<String> runCodes;
	public String runCode;
	
	public Set<Integer> laneNumbers;
	public Integer laneNumber;
	
	
	public String bioinformaticValidCode;
	public String productionValidCode;
	
	public Set<String> runTypeCodes;
	
	public Set<String> sampleTypeCodes;
	public Set<String> sampleCategoryCodes;
	
	
	public Date fromDate;
	public Date toDate;
	
	public String code;
	public Set<String> codes;
	public Set<String> supportCodes;
	public String regexCode;
	public String regexSampleCode;
	public String regexSupportCode;
    
    public Set<String> instrumentCodes;	
    public Set<String> productionResolutionCodes;
    public Set<String> bioinformaticResolutionCodes;
    
    public String productionValuationUser;
    public String productionValuationCriteriaCode;
    
    public String location;
    
    public String regexArchiveId;
    public Set<String> archiveIds;
    
    
    public String ncbiScientificName;
    public String ncbiScientificNameRegex;
    
    public Set<String> existingFields, notExistingFields;
    
    //public String isSentCCRT, isSentCollaborator;

    public Map<String, List<String>> properties = new HashMap<String, List<String>>();
    public Map<String, List<String>> sampleOnContainerProperties = new HashMap<String, List<String>>();
    public Map<String, Map<String, List<String>>> treatmentProperties = new HashMap<String, Map<String, List<String>>>();
    
}
