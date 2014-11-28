package builder.data;

import java.util.Date;

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
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class,codeSub1);
		Submission submission = new SubmissionBuilder()
									.withCode(codeSub1)
									.withSubmissionDirectory(System.getProperty("user.home")+"/NGL-SUB-Test/subDir")
									.withSubmissionDate(new Date())
									.withState(new StateBuilder()
												.withCode("IN_WAITING")
												.build())												
									.build();
		MongoDBDAO.save(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submission);
		
		//Log data submitted
		Submission submissionDB = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, codeSub1);
		Logger.info("Submission "+submissionDB.code+","+submissionDB.submissionDirectory+","+submissionDB.submissionDate);
		
	}
}
