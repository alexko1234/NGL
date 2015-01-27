package builder.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.sra.experiment.instance.Experiment;
import models.sra.sample.instance.Sample;
import models.sra.submission.instance.Submission;
import models.utils.InstanceConstants;

import org.junit.Test;

import play.Logger;
import utils.AbstractTestData;
import fr.cea.ig.MongoDBDAO;

public class InitData extends AbstractTestData{

	
	@Test
	public void initDataDevForWorkflow() throws ParseException
	{
		//Create Submission for submission
		String codeSub1 = "codeSub1";
		String codeExp1 = "codeExp1";
		String codeExp2 = "codeExp2";
		String codeSamp1 = "codeSamp1";
		String codeRun1 = "codeRun1";
		String codeRun2 = "codeRun2";
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date dateSubmission =sdf.parse("26/01/2015");
		
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class,codeSub1);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Submission.class,codeExp1);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Submission.class,codeExp2);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, codeSamp1);
		
		
		
		Submission submission = new SubmissionBuilder()
									.withCode(codeSub1)
									.withSubmissionDirectory(System.getProperty("user.home")+"/NGL-SUB-Test/subDir")
									.withSubmissionDate(dateSubmission)
									.withState(new StateBuilder()
												.withCode("IN_WAITING")
												.withUser("ejacoby@genoscope.cns.fr")
												.build())
									.addExperimentCode(codeExp1)
									.addExperimentCode(codeExp2)
									.addSampleCode(codeSamp1)
									.addRunCode(codeRun1)
									.addRunCode(codeRun2)
									.build();
		
		MongoDBDAO.save(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submission);
		
		Experiment experiment1 = new ExperimentBuilder()
									.withCode(codeExp1)
									.withRun(new RunBuilder()
										.withCode(codeRun1)
										.addRawData(new RawDataBuilder()
											.withRelatifName("file1.fastq.gz").build())
										.addRawData(new RawDataBuilder()
											.withRelatifName("file2.fastq.gz").build())
										.build())
									.build();
		
		Experiment experiment2 = new ExperimentBuilder()
								.withCode(codeExp2)
								.withRun(new RunBuilder()
									.withCode(codeRun2)
										.addRawData(new RawDataBuilder()
											.withRelatifName("file3.fastq.gz").build())
										.addRawData(new RawDataBuilder()
											.withRelatifName("file4.fastq.gz").build())
									.build())
								.build();
		MongoDBDAO.save(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, experiment1);
		MongoDBDAO.save(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, experiment2);
		
		
		Sample sample1 = new SampleBuilder()
						.withCode(codeSamp1)
						.build();
		MongoDBDAO.save(InstanceConstants.SRA_SAMPLE_COLL_NAME, sample1);
		
		//Log data submitted
		Submission submissionDB = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, codeSub1);
		Logger.info("Submission "+submissionDB.code+","+submissionDB.submissionDirectory+","+submissionDB.submissionDate);
		
	}
}
