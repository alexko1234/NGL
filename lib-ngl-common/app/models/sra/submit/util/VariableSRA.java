package models.sra.submit.util;



import java.util.HashMap;
import java.util.Map;

import play.Play;

public interface VariableSRA {
	SraParameter sraParam = new SraParameter();
	

	static final String centerName = Play.application().configuration().getString("centerName");
	static final String laboratoryName = Play.application().configuration().getString("laboratoryName");
	static final String submissionRootDirectory = Play.application().configuration().getString("submissionRootDirectory");
	static final String defaultLibraryConstructionProtocol = Play.application().configuration().getString("defaultLibraryConstructionProtocol");
	static final String admin = Play.application().configuration().getString("admin");
	static final String xmlSubmission = Play.application().configuration().getString("xmlSubmission");
	static final String xmlStudies = Play.application().configuration().getString("xmlStudies");
	static final String xmlSamples = Play.application().configuration().getString("xmlSamples");
	static final String xmlExperiments = Play.application().configuration().getString("xmlExperiments");
	static final String xmlRuns = Play.application().configuration().getString("xmlRuns");
	
	static final Map<String, String> mapCenterName = sraParam.getParameter("centerName");
	static final Map<String, String> mapLaboratoryName = sraParam.getParameter("laboratoryName");
	static final Map<String, String> mapLibProcessTypeCodeVal_orientation = sraParam.getParameter("libProcessTypeCodeValue_orientation");
	static final Map<String, String> mapTypeReadset = sraParam.getParameter("typeReadset"); 
	static final Map<String, String> mapExistingStudyType = sraParam.getParameter("existingStudyType");
	static final Map<String, String> mapLibraryLayoutOrientation = sraParam.getParameter("libraryLayoutOrientation");
	static final Map<String, String> mapLibrarySource = sraParam.getParameter("librarySource");
	static final Map<String, String> mapLibraryStrategy = sraParam.getParameter("libraryStrategy");
	static final Map<String, String> mapLibrarySelection = sraParam.getParameter("librarySelection");
	static final Map<String, String> mapTypePlatform = sraParam.getParameter("typePlatform");
	static final Map<String, String> mapLibraryLayout = sraParam.getParameter("libraryLayout");
	static final Map<String, String> mapInstrumentModel = sraParam.getParameter("instrumentModel"); 
	static final Map<String, String> mapAnalysisFileType =  sraParam.getParameter("analysisFileType"); 

	static final Map<String, String> mapStrategySample =  new HashMap<String, String>() {
		{
			put("strategy_external_sample", "strategy_external_sample"); // Si pas de sample à creer parce que fournis par les collaborateurs
			put("strategy_sample_taxon", "strategy_sample_taxon"); // si sample specifique par code_projet et taxon
			put("strategy_sample_clone", "strategy_sample_clone"); // si sample specifique par code_projet et clone
		}
	};
	
	static final Map<String, String> mapStrategyStudy =  new HashMap<String, String>() {
		{
			put("strategy_external_study", "strategy_external_study"); // Si pas de study à creer parce que fournis par les collaborateurs
			put("strategy_internal_study", "strategy_internal_study"); 
		}
	};	
		
	
};


