package controllers.containers.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controllers.ListForm;

public class ContainerSupportsSearchForm extends ListForm {
	public String code;
	public List<String> codes;
	public String codeRegex;
	public String containerSupportCategory;
	public List<String> containerSupportCategories;
	public String categoryCode;
	public String stateCode;
	public List<String> stateCodes;
	public String nextExperimentTypeCode;
	public String processTypeCode;
	public List<String> projectCodes;
	public List<String> sampleCodes;
	public List<String> fromTransformationTypeCodes;
	public String createUser;
	//public List<String> valuations;   23/05/2016 FDS supression
	public Date fromDate;
	public Date toDate;
	public List<String> users;
	public String storageCodeRegex;     //  23/05/2016 FDS ajout
	public String storageCode;          //  23/05/2016 FDS ajout
	public Map<String, List<String>> properties = new HashMap<String, List<String>>();
	
	@Override
	public String toString() {
		// 23/05/2016 FDS NGL-825 ajout storageCode
		return "SupportsSearchForm ["
				+ "  code=" + code 
				+ ", categoryCode="+ categoryCode 
				+ ", stateCode=" + stateCode
				+ ", nextExperimentTypeCode=" + nextExperimentTypeCode
				+ ", processTypeCode=" + processTypeCode 
				+ ", projectCodes="+ projectCodes 
				+ ", sampleCodes=" + sampleCodes
				+ ", containerSupportCategory=" + containerSupportCategory
				+ ", containerSupportCategories=" + containerSupportCategories
				+ ", fromTransformationTypeCodes=" + fromTransformationTypeCodes
				+ ", fromDate=" + fromDate 
				+ ", toDate=" + toDate
				+ ", createUser=" + createUser 
				+ ", users=" + users 
				+ ", storageCode="+ storageCode
				+ "]";
	}
}
