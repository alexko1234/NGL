package controllers.experiments.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import controllers.ListForm;

public class ExperimentSearchForm extends ListForm{
	public String code;
	public Set<String> codes;
	public String typeCode;
	public String categoryCode;
	public String projectCode;
	public Set<String> projectCodes;
	public Set<String> sampleCodes;
	public String sampleCode;
	public Date fromDate;
	public Date toDate;
	public String stateCode;
	public Set<String> stateCodes;
	public Set<String> users;
	public String containerSupportCode;
	public Set<String> containersCodes;	
	public String atomicTransfertMethods;
	public String reagentOrBoxCode;
	public String instrument;
	
	public Set<String> tags;
	public Set<String> sampleTypeCodes;
	public Map<String, List<String>> atomicTransfertMethodsInputContainerUsedsContentsProperties = new HashMap<String, List<String>>();
	

	
	@Override
	public String toString() {
		return "ExperimentSearchForm [code="+code+", codes="+ codes +", typeCode=" + typeCode
				+ ", categoryCode="	+ categoryCode + ", projectCodes=" + projectCodes
				+ ", sampleCodes=" + sampleCodes + ", fromDate=" + fromDate
				+ ", toDate=" + toDate + ", stateCode=" + stateCode
				+ ", users=" + users +", containerSupportCode=" + containerSupportCode +", containersCodes=" + containersCodes
				+ ", atomicTransfertMethods="+ atomicTransfertMethods + ", instrument"+ instrument + "]";
	}
}
