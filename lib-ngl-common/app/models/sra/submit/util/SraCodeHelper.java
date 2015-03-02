package models.sra.submit.util;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.run.instance.ReadSet;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;


public class SraCodeHelper extends CodeHelper {

	
	public SraCodeHelper()
	{}
    private static class SingletonHolder
	{
		private final static SraCodeHelper instance = new SraCodeHelper();
	}
    
	public static SraCodeHelper getInstance()
	{			
		return SingletonHolder.instance;
	}

	public synchronized String generateConfigurationCode(String projectCode) {
		// conf_projectCode_YYYYMMDDHHMMSSSS
		return ("conf_" + projectCode + "_" + this.getInstance().generateBarCode()).toUpperCase();
	}
	
	public synchronized String generateStudyCode(String projectCode) {
		// study_projectCode_YYYYMMDDHHMMSSSS
		return ("study_" + projectCode + "_" + this.getInstance().generateBarCode()).toUpperCase();
	}
	
	public synchronized String generateSubmissionCode(String projectCode) {
		// cns_projectCode_YYYYMMDDHHMMSSSS
		return ("cns_" + projectCode + "_" + this.getInstance().generateBarCode()).toUpperCase();
	}
	
	public String generateExperimentCode(String readSetCode) {
		// exp_readSetCode
		return ("exp_" + readSetCode);
	}
	
	public String generateRunCode(String readSetCode) {
		// run_readSetCode
		return ("run_" + readSetCode);
	}
	
	public String generateSampleCode(ReadSet readSet, String projectCode, String strategySample) {
		String laboratorySampleCode = readSet.sampleCode;
		models.laboratory.sample.instance.Sample laboratorySample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, models.laboratory.sample.instance.Sample.class, laboratorySampleCode);
		String laboratorySampleName = laboratorySample.name;

		String clone = laboratorySample.referenceCollab;
		String taxonId = laboratorySample.taxonCode;

		String laboratoryRunCode = readSet.runCode;
		models.laboratory.run.instance.Run  laboratoryRun = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, models.laboratory.run.instance.Run.class, laboratoryRunCode);

		String codeSample = null;
		if (strategySample.equalsIgnoreCase("STRATEGY_SAMPLE_CLONE")) {
			codeSample = "sample_" + projectCode + "_" + taxonId + "_" + clone;
		} else if (strategySample.equalsIgnoreCase("STRATEGY_SAMPLE_TAXON")) {
			codeSample = "sample_" + projectCode + "_" + taxonId;
		} else if (strategySample.equalsIgnoreCase("STRATEGY_NO_SAMPLE")) {
			//envisager d'avoir des fichiers de correspondance 
		} else {
			// Declencher une erreur.
		}	
		return codeSample;

	}
}
