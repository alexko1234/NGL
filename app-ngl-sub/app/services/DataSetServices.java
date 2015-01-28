package services;

import models.laboratory.common.instance.TraceInformation;
import validation.ContextValidation;
import models.sra.submit.common.instance.Sample;
import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.sra.instance.Configuration;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.util.SraException;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
import models.laboratory.common.instance.State;
import models.laboratory.run.instance.ReadSet;
import play.Logger;
import services.SubmissionServices;
import fr.cea.ig.MongoDBDAO;



public class DataSetServices {

	public static void createDataSetForFileAcServices()throws SraException {
		// Creation d'une soumission complete en attente des numeros d'accession :
		String status = "inProgress"; // a changer pour waitingAc ?
		
		String studyCode = "test_study_AWK";
		String configCode = "test_conf_AWK";
		String submissionCode="test_cns_AWK";
		String readSetCode = "AWK_EMOSW_1_H9YKWADXX.IND1"; // lotSeqName pairé et avec mapping
		String experimentCode = "test_exp_" + readSetCode;
		String sampleCode = "test_sample_AWK_472";
		String runCode = "test_run_" + readSetCode;
		String submissionDirectory = "/env/cns/submit_traces/SRA/ngl-sub/mesTests/";

		// Creer objet config valide avec status = userValidate pour test :
		SubmissionServices SubmissionServices = new SubmissionServices();
		String user = "william";
		ContextValidation contextValidation = new ContextValidation(user);
		contextValidation.setCreationMode();		
		Configuration config = new Configuration();
		config.code = configCode;
		config.projectCode = "AWK";
		config.strategySample = "strategy_sample_taxon";
		config.librarySelection = "random";
		config.librarySource = "genomic";
		config.libraryStrategy = "wgs";
		config.traceInformation = new TraceInformation(); 
		config.traceInformation.setTraceInformation(user);
		config.state = new State("userValidate", user);
		config.validate(contextValidation);
		MongoDBDAO.save(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, config);
		contextValidation.displayErrors(Logger.of("SRA"));
		
		// Creer un study valide avec un status inWaiting (on ne prend pas status userValidate) et le sauver dans mongodb:
		Study study = new Study();
		study.centerName=VariableSRA.centerName;
		study.projectCode = "AWK";
		study.centerProjectName = "AWK";
		study.code = studyCode;
		study.existingStudyType="Metagenomics";
		study.traceInformation.setTraceInformation(user);
		study.state = new State(status, user);
		study.validate(contextValidation);
		contextValidation.displayErrors(Logger.of("SRA"));
		MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);

		// creer un objet Experiment (avec son run associé) valide avec un status "inWaiting" et le sauver dans mongodb :
		ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetCode);
		Experiment experiment = SubmissionServices.createExperimentEntity(readSet, config.projectCode, "william");
		// changer le code de experiment :
		experiment.code = experimentCode;
		experiment.sampleCode = sampleCode;
		experiment.studyCode = studyCode;
		experiment.run.code = runCode;
		experiment.librarySelection = config.librarySelection;
		experiment.librarySource = config.librarySource;
		experiment.libraryStrategy = config.libraryStrategy;
		experiment.state = new State(status, user);
		experiment.validate(contextValidation);
		contextValidation.displayErrors(Logger.of("SRA"));
		MongoDBDAO.save(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, experiment);

		// Creer un objet sample avec status inWaiting et le sauver dans mongodb :
		Sample sample = new Sample();
		sample.code = sampleCode;
		sample.taxonId = new Integer(472);
		sample.clone = "Acineto_cDNA_SMARTST_1ng_Ctrl";
		sample.projectCode = config.projectCode;
		sample.state = new State(status, user);
		sample.traceInformation.setTraceInformation(user);		
		sample.validate(contextValidation);
		contextValidation.displayErrors(Logger.of("SRA"));
		MongoDBDAO.save(InstanceConstants.SRA_SAMPLE_COLL_NAME, sample);

		// Creer un objet submission avec status inWaiting et le sauver dans mongodb :
		//Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submission.instance.Submission.class,  submissionCode);
		Submission submission = new Submission(config.projectCode, user);
		submission.submissionDirectory = submissionDirectory;
		submission.code = submissionCode;
		//System.out.println("submissionCode="+ submissionCode);
		submission.state = new State("new", user);
		submission.config = config;
		submission.studyCode = studyCode;
		submission.sampleCodes.add(sampleCode);
		submission.experimentCodes.add(experimentCode);
		submission.runCodes.add(runCode);
		submission.state = new State(status, user);
		submission.validate(contextValidation);
		contextValidation.displayErrors(Logger.of("SRA"));
		MongoDBDAO.save(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submission);
	}

	
	public static void deleteDataSetForFileAcServices()throws SraException {
		String studyCode = "test_study_AWK";
		String configCode = "test_conf_AWK";
		String submissionCode="test_cns_AWK";
		String readSetCode = "AWK_EMOSW_1_H9YKWADXX.IND1"; // lotSeqName pairé et avec mapping
		String experimentCode = "test_exp_" + readSetCode;
		String sampleCode = "test_sample_AWK_472";
		String runCode = "test_run_" + readSetCode;
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.submit.common.instance.Study.class, studyCode);		
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, models.sra.submit.common.instance.Sample.class, sampleCode);		
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, models.sra.submit.sra.instance.Configuration.class, configCode);		
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, models.sra.submit.sra.instance.Experiment.class, experimentCode);		
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submit.common.instance.Submission.class, submissionCode);		
	}
}
