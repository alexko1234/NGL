package SraValidation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.sra.configuration.instance.Configuration;
import models.sra.experiment.instance.Experiment;
import models.sra.study.instance.Study;
import models.sra.submission.instance.Submission;
import models.sra.utils.SraException;
import models.sra.utils.VariableSRA;
import models.utils.InstanceConstants;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.ReadSet;

import org.junit.Test;

import play.Logger;

import fr.cea.ig.MongoDBDAO;

import services.FileAcServices;
import services.SubmissionServices;
import utils.AbstractTestsSRA;
import validation.ContextValidation;

import org.junit.Assert;
import org.junit.Test;


public class FileAcServicesTest  extends AbstractTestsSRA {
	
	@Test
	public void FileAcServicesSuccess() throws IOException, SraException {
		//fileAcServices.traitementFileAC(submissionCode, new File(fileName));
		String studyCode = "study_AWK_5";
		String configCode = "conf_AWK_5";
		String subCode="cns_AWK_16_12_2014_1";
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.study.instance.Study.class, studyCode);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, models.sra.configuration.instance.Configuration.class, configCode);
		SubmissionServices.cleanDataBase(subCode);

		
		SubmissionServices submissionServices = new SubmissionServices();
		String user = "william";
		ContextValidation contextValidation = new ContextValidation(user);
		contextValidation.setCreationMode();		
		Configuration config = new Configuration();
		config.code = "conf_AWK_5";
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
		
		// Creer un study validé par utilisateur et le sauver dans mongodb:
		Study study = new Study();
		study.centerName=VariableSRA.centerName;
		study.projectCode = "AWK";
		study.centerProjectName = "AWK";
		study.code = "study_AWK_5";
		study.existingStudyType="Metagenomics";
		study.traceInformation.setTraceInformation(user);
		study.state = new State("userValidate", user);
		study.validate(contextValidation);
		contextValidation.displayErrors(Logger.of("SRA"));
		MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);

		String codeReadSet4 = "AWK_EMOSW_1_H9YKWADXX.IND1"; // lotSeqName pairé et avec mapping
		//Ex de donnée pairee avec mapping :
		ReadSet readSet4 = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, codeReadSet4);
		List<ReadSet> readSets = new ArrayList<ReadSet>();
	
		readSets.add(readSet4);

		System.out.println("Create new submission for readSet " + readSet4.code);
		System.out.println("submissionServices.createNewSubmission("+config.projectCode+", readSets,"+ study.code+","+ config.code+", \"william\");");
		String submissionCode = submissionServices.createNewSubmission(config.projectCode, readSets, study.code, config.code, "william");
		

		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submission.instance.Submission.class,  submissionCode);

		String resultDirectory = "/env/cns/submit_traces/SRA/ngl-sub/mesTests2/";

		//XmlServices xmlServices = new XmlServices();
		//XmlServices.writeAllXml(submissionCode);
		

		String fileName = "/env/cns/submit_traces/SRA/ngl-sub/mesTests/RESULT_AC";
		FileAcServices fileAcServices = new FileAcServices();
		System.out.println("submissionCode="+ submissionCode);
		System.out.println("submission.studyCode="+submission.studyCode);
		for (String expCode: submission.experimentCodes) {
			System.out.println("submission.experimentCode="+expCode);
			Experiment exp = MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, expCode);
			System.out.println("submission.runCode" + exp.run.code);
		}
		for (String sampleCode: submission.sampleCodes) {
			System.out.println("submission.sampleCode="+sampleCode);
		}
		for (String sampleCode: submission.sampleCodes) {
			System.out.println("submission.sampleCode="+sampleCode);
		}
		fileAcServices.traitementFileAC(submissionCode, new File(fileName));
		System.out.println("submission.accession="+submission.accession);
		System.out.println("\ndisplayErrors pour validationXmlServicesSuccess :");
		
		contextValidation.displayErrors(Logger.of("SRA"));		
		Assert.assertTrue(contextValidation.errors.size()==0); // si aucune erreur
		
		/*		MongoDBDAO.deleteByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.study.instance.Study.class, study.code);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, models.sra.configuration.instance.Configuration.class, config.code);
		SubmissionServices.cleanDataBase(submission.code);
*/
		}
}
