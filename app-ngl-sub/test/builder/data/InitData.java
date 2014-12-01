package builder.data;

import java.util.Date;

import models.sra.experiment.instance.Experiment;
import models.sra.submission.instance.Submission;
import models.utils.InstanceConstants;

import org.junit.Test;

import play.Logger;
import fr.cea.ig.MongoDBDAO;
import utils.AbstractTestData;

public class InitData extends AbstractTestData{

	
	@Test
	public void initDataDevForWorkflow()
	{
		//Create Submission for submission
		String codeSub1 = "codeSub1";
		String codeExp1 = "codeExp1";
		String codeExp2 = "codeExp2";
		
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class,codeSub1);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Submission.class,codeExp1);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Submission.class,codeExp2);
		Submission submission = new SubmissionBuilder()
									.withCode(codeSub1)
									.withSubmissionDirectory(System.getProperty("user.home")+"/NGL-SUB-Test/subDir")
									.withSubmissionDate(new Date())
									.withState(new StateBuilder()
												.withCode("IN_WAITING")
												.build())
									.addExperimentCode(codeExp1)
									.addExperimentCode(codeExp2)
									.build();
		
		MongoDBDAO.save(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submission);
		
		Experiment experiment1 = new ExperimentBuilder()
		.withCode(codeExp1)
		.withRun(new RunBuilder()
					.withCode("codeRun1")
					.addRawData(new RawDataBuilder()
								.withRelatifName("file1.fastq.gz").build())
					.addRawData(new RawDataBuilder()
								.withRelatifName("file2.fastq.gz").build())
					.build())
		.build();
		
		Experiment experiment2 = new ExperimentBuilder()
		.withCode(codeExp2)
		.withRun(new RunBuilder()
					.withCode("codeRun2")
					.addRawData(new RawDataBuilder()
								.withRelatifName("file3.fastq.gz").build())
					.addRawData(new RawDataBuilder()
								.withRelatifName("file4.fastq.gz").build())
					.build())
		.build();
		MongoDBDAO.save(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, experiment1);
		MongoDBDAO.save(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, experiment2);
		
		//Log data submitted
		Submission submissionDB = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, codeSub1);
		Logger.info("Submission "+submissionDB.code+","+submissionDB.submissionDirectory+","+submissionDB.submissionDate);
		
	}
}
