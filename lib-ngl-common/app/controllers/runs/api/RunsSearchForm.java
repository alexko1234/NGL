package controllers.runs.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controllers.ListForm;

public class RunsSearchForm extends ListForm {
	
    public List<String> codes;
    public String code;
    public String regexCode;
    
    public List<String> categoryCodes;
    public String categoryCode;
    
    public List<String> stateCodes;
    public String stateCode;

    public List<String> typeCodes;

    public List<String> projectCodes;
    public String projectCode;
    
    public List<String> sampleCodes;
    public String sampleCode;
    
    public String containerSupportCode;
    public List<String> containerSupportCodes;
    
    public String validCode;

    public Date fromDate;
    public Date toDate;
    
    public Date fromEndRGDate;
    public Date toEndRGDate;
    
    
    public String valuationUser;
    public String valuationCriteriaCode;
    
    public Boolean keep;
    
    public List<String> instrumentCodes;
    public List<String> runResolutionCodes;
    public List<String> laneResolutionCodes;
    public List<String> resolutionCodes;
    
    public List<String> existingFields, notExistingFields;
    
    public Map<String, List<String>> properties = new HashMap<>();
    public Map<String, Map<String, List<String>>> treatmentProperties = new HashMap<>();
    public Map<String, Map<String, List<String>>> treatmentLanesProperties = new HashMap<>();
}
