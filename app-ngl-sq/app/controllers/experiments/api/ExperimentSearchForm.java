package controllers.experiments.api;

import java.util.Date;
import java.util.List;

import controllers.ListForm;

public class ExperimentSearchForm extends ListForm{
	public String code;
	public List<String> codes;
	public String typeCode;
	public String categoryCode;
	public String projectCode;
	public List<String> projectCodes;
	public List<String> sampleCodes;
	public String sampleCode;
	public Date fromDate;
	public Date toDate;
	public String stateCode;
	public List<String> stateCodes;
	public List<String> users;
	public String containerSupportCode;
	public List<String> containersCodes;	
	public String atomicTransfertMethods;
	public String reagentOrBoxCode;
	public String instrument;
	
	public List<String> tags;
	
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
