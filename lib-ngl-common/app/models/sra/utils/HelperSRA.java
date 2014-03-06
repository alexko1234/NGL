package models.sra.utils;

import models.sra.study.instance.Study;
import models.sra.sample.instance.Sample;
import models.sra.experiment.instance.Experiment;
import models.utils.InstanceConstants;
import fr.cea.ig.MongoDBDAO;

public class HelperSRA {
	
	// Verifie que le codeReadSet existe dans la collection Experiment
	public static Boolean checkCodeReadSetExistInExperimentCollection(String codeReadSet){
		return MongoDBDAO.checkObjectExist(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "readSetCode", codeReadSet);
	}

	// Verifie que le codeRun existe dans la collection Experiment
	public static Boolean checkCodeRunExistInExperimentCollection(String codeRun){
		return MongoDBDAO.checkObjectExist(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "runCode", codeRun);
	}	
	
	// Verifie que le codeExperiment existe bien dans la collection Experiment
	public static Boolean checkCodeExperimentExistInExperimentCollection(String codeExperiment) {
		return MongoDBDAO.checkObjectExist(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "code", codeExperiment);
	}
	
	// Verifie que le codeSample existe bien dans la collection Sample
	public static Boolean checkCodeSampleExistInSampleCollection(String codeSample) {
		return MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, "code", codeSample);
	}	
	
	// Verifie que le codeStudy existe bien dans la collection Study
	public static Boolean checkCodeStudyExistInStudyCollection(String codeStudy) {
		return MongoDBDAO.checkObjectExist(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, "code", codeStudy);
	}
}