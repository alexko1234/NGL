package SraValidation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.ReadSet;
import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.sra.instance.Configuration;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.util.SraException;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;

import org.junit.Assert;
import org.junit.Test;

import play.Logger;
import services.SubmissionServices;
import services.XmlServices;
import utils.AbstractTestsSRA;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;
public class XmlServicesTest extends AbstractTestsSRA {
	
	@Test
	public void validationXmlServicesSuccess() throws IOException, SraException {

		SubmissionServices submissionServices = new SubmissionServices();
		String user = "william";	
		ContextValidation contextValidation = new ContextValidation(user);
		contextValidation.setCreationMode();
		contextValidation.getContextObjects().put("type", "sra");
		Configuration config = new Configuration();
		config.code = "conf_AWK_5_2";
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
		study.code = "study_AWK_5_2";
		study.existingStudyType="Metagenomics";
		study.traceInformation.setTraceInformation(user);
		study.code = "study_" + config.projectCode;
		study.state = new State("userValidate", user);
		contextValidation = new ContextValidation(user);
		contextValidation.setCreationMode();
		contextValidation.getContextObjects().put("type", "sra");
		study.validate(contextValidation);
		contextValidation.displayErrors(Logger.of("SRA"));
		MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);
		
		
		String codeReadSet1 = "AUP_COSW_4_D09BTACXX.IND7";   // equivalent lotSeqName 
		String codeReadSet2 = "AUP_NAOSW_5_C0UW4ACXX.IND10"; // equivalent lotSeqName 
		String codeReadSet3 = "AKL_ABOSA_1_80MJ3ABXX"; // equivalent lotSeqName 
		String codeReadSet4 = "AWK_EMOSW_1_H9YKWADXX.IND1"; // lotSeqName pairé et avec mapping
		
	
		System.out.println("READSET4="+codeReadSet4);
		List<String> readSetCodes = new ArrayList<String>();
		
		readSetCodes.add(codeReadSet4);

		System.out.println("Create new submission for readSet " + codeReadSet4);
		contextValidation = new ContextValidation(userContext);
		contextValidation.setCreationMode();
		contextValidation.getContextObjects().put("type", "sra");
		String submissionCode = submissionServices.initNewSubmission(config.projectCode, readSetCodes, study.code, config.code, "william", contextValidation);
	
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submit.common.instance.Submission.class,  submissionCode);
		System.out.println("Submission " + submission.code);

		// Simuler la fonction activate e, ajoutant les refSampleCodes dans submission.sampleCodes
		for (String sampleCode: submission.refSampleCodes) {
			submission.sampleCodes.add(sampleCode);
		}        
		// Sauver la soumission avec son bon sampleCodes :
		MongoDBDAO.save(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submission);

		
		String resultDirectory_1 = "/env/cns/submit_traces/SRA/ngl-sub/mesTests/";
		File dataRep_1 = new File(resultDirectory_1);
		if (!dataRep_1.exists()){
			dataRep_1.mkdirs();	
		}		
		
		String resultDirectory = "/env/cns/submit_traces/SRA/ngl-sub/mesTests2/";
		File dataRep = new File(resultDirectory);
		if (!dataRep.exists()){
			dataRep.mkdirs();	
		}
		File studyFile = new File(resultDirectory+"study.xml");
		File sampleFile = new File(resultDirectory+"sample.xml");
		File experimentFile = new File(resultDirectory+"experiment.xml");
		File runFile = new File(resultDirectory+"run.xml");
		File submissionFile = new File(resultDirectory+"submission.xml");

		/*XmlServices.writeStudyXml(submission, studyFile);
		XmlServices.writeSampleXml(submission, sampleFile);
		XmlServices.writeExperimentXml(submission, experimentFile);
		XmlServices.writeRunXml(submission, runFile);
		XmlServices.writeSubmissionXml(submission, submissionFile);
		*/
		
		XmlServices.writeAllXml(submissionCode, resultDirectory);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.submit.common.instance.Study.class, study.code);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, models.sra.submit.sra.instance.Configuration.class, config.code);
		SubmissionServices.cleanDataBase(submission.code);

		//XmlServices xmlServices = new XmlServices();
		//XmlServices.writeAllXml(submissionCode);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.submit.common.instance.Study.class, study.code);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, models.sra.submit.sra.instance.Configuration.class, config.code);
		
		System.out.println("\ndisplayErrors pour validationXmlServicesSuccess :");
		contextValidation.displayErrors(Logger.of("SRA"));		
		Assert.assertTrue(contextValidation.errors.size()==0); // si aucune erreur
	}
}
