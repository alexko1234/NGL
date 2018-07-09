package controllers.experiments.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import controllers.ListForm;

public class ExperimentSearchForm extends ListForm {
	
	public String code;
	public Set<String> codes;
	public String typeCode;
	public Set<String> typeCodes;
	public String categoryCode;
	public String projectCode;
	public Set<String> projectCodes;
	public Set<String> sampleCodes;
	public String sampleCode;
	public Date fromDate;
	public Date toDate;
	public String stateCode;
	public Set<String> stateCodes;
	public List<String> stateResolutionCodes;
	public Set<String> users;
	public String containerSupportCode;
	public Set<String> containerSupportCodes;
	public String containerSupportCodeRegex;
	
	public String containerCode;
	public Set<String> containerCodes;
	public String containerCodeRegex;
	
	public String containerFromTransformationTypeCode;
	public String atomicTransfertMethods;
	public String reagentOrBoxCode;
	
	public String instrument;
	public Set<String> instruments;
	
	public String instrumentCode;
	public Set<String> instrumentCodes;
	
	public Set<String> protocolCodes;
	
	
	public Set<String> sampleTypeCodes;
	public Map<String, List<String>> atomicTransfertMethodsInputContainerUsedsContentsProperties = new HashMap<>();
	
	public Map<String, List<String>> experimentProperties = new HashMap<>();
	public Map<String, List<String>> instrumentProperties = new HashMap<>();
	
	
	// FDS 21/08/2015 pour debug only???  ajouter tags et sampleTypeCodes dans le return 
	@Override
	public String toString() {
		return "ExperimentSearchForm [code="+code+", codes="+ codes +", typeCode=" + typeCode
				+ ", categoryCode="	+ categoryCode + ", projectCodes=" + projectCodes
				+ ", sampleCodes=" + sampleCodes + ", fromDate=" + fromDate
				+ ", toDate=" + toDate + ", stateCode=" + stateCode
				+ ", users=" + users +", containerSupportCode=" + containerSupportCode 
				+ ", atomicTransfertMethods="+ atomicTransfertMethods + ", instrument"+ instrument 
				+ ", sampleTypeCodes="+ sampleTypeCodes
				+ "]";
	}
}
