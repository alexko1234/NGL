package models.sra.submit.util;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.run.instance.ReadSet;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.code.DefaultCodeImpl;

public class SraCodeHelper extends DefaultCodeImpl {

//	public SraCodeHelper() { 
//	}
	private SraCodeHelper() { 
	}
	
    private static class SingletonHolder {
		private final static SraCodeHelper instance = new SraCodeHelper();
	}
    
	public static SraCodeHelper getInstance() {			
		return SingletonHolder.instance;
	}

	// TODO: check synchronized keyword as it seems overkill
	public synchronized String generateConfigurationCode(List<String> projectCodes) throws SraException {
		// conf_projectCode1_projectCode_2_YYYYMMDDHHMMSSSS
		String code = "";
		for (String projectCode: projectCodes) {
			if (StringUtils.isNotBlank(projectCode)) {
				 code += "_" + projectCode;
			}
		}
		if (StringUtils.isBlank(code)) {
			throw new SraException("generateConfigurationCode : argument sans aucune valeur ???");
		}
//		return ("conf" + code + "_" + this.getInstance().generateBarCode()).toUpperCase();
		return ("conf" + code + "_" + SraCodeHelper.getInstance().generateBarCode()).toUpperCase();
	}	
		
	// TODO: check synchronized keyword as it seems overkill
	public synchronized String generateStudyCode(List<String> projectCodes) throws SraException {
		// conf_projectCode1_projectCode_2_YYYYMMDDHHMMSSSS
		String code = "";
		for (String projectCode: projectCodes) {
			if (StringUtils.isNotBlank(projectCode)) {
				 code += "_" + projectCode;
			}
		}
		if (StringUtils.isBlank(code)){
			throw new SraException("generateStudyCode : argument sans aucune valeur ???");
		}
//		return ("study" + code + "_" + this.getInstance().generateBarCode()).toUpperCase();
		return ("study" + code + "_" + SraCodeHelper.getInstance().generateBarCode()).toUpperCase();
	}	
	
	// TODO: check synchronized keyword as it seems overkill
	public synchronized String generateSubmissionCode(List<String> projectCodes) throws SraException {
		// cns_projectCode1_projectCode_2_YYYYMMDDHHMMSSSS
		String code = "";
		for (String projectCode: projectCodes) {
			if (StringUtils.isNotBlank(projectCode)) {
				 code += "_" + projectCode;
			}
		}
		if (StringUtils.isBlank(code)){
			throw new SraException("generateSubmissionCode : argument sans aucune valeur ???");
		}
//		return ("GSC" + code + "_" + this.getInstance().generateBarCode()).toUpperCase();
		return ("GSC" + code + "_" + SraCodeHelper.getInstance().generateBarCode()).toUpperCase();
	}
	
	public String generateExperimentCode(String readSetCode) {
		// exp_readSetCode
		return "exp_" + readSetCode;
	}
	
	public String generateRunCode(String readSetCode) {
		// run_readSetCode
		return "run_" + readSetCode;
	}
	
	public String generateExternalSampleCode(String sampleAc) throws SraException {
		return "externalSample_" + sampleAc;
	}
	
	public String generateExternalStudyCode(String studyAc) throws SraException {
		return "externalStudy_" + studyAc;
	}
		
	public String generateSampleCode(ReadSet readSet, String projectCode, String strategySample) throws SraException {
		if (readSet == null)
			throw new SraException("Aucun readSet en argument");
		if (StringUtils.isBlank(projectCode))
			throw new SraException("Aucun projectCode en argument");
		if (StringUtils.isBlank(strategySample))
			throw new SraException("Aucun strategySample en argument");
		
		String laboratorySampleCode = readSet.sampleCode;
		models.laboratory.sample.instance.Sample laboratorySample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, models.laboratory.sample.instance.Sample.class, laboratorySampleCode);
		//String laboratorySampleName = laboratorySample.name;

		String clone = laboratorySample.referenceCollab;
		String taxonId = laboratorySample.taxonCode;

		//String laboratoryRunCode = readSet.runCode;
		//models.laboratory.run.instance.Run  laboratoryRun = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, models.laboratory.run.instance.Run.class, laboratoryRunCode);

		String codeSample = null;
		
		if (strategySample.equalsIgnoreCase("STRATEGY_SAMPLE_CLONE")) {
			codeSample = "sample_" + projectCode + "_" + taxonId + "_" + clone;
		} else if (strategySample.equalsIgnoreCase("STRATEGY_SAMPLE_TAXON")) {
			codeSample = "sample_" + projectCode + "_" + taxonId;
		} else if (strategySample.equalsIgnoreCase("STRATEGY_EXTERNAL_SAMPLE")) {
			throw new SraException("STRATEGY_EXTERNAL_SAMPLE :  + utiliser generateExternalSampleCode");		
		} else {
			throw new SraException("StrategySample inconnu : " + strategySample);		
		}	
		return codeSample;
	}
	
}
