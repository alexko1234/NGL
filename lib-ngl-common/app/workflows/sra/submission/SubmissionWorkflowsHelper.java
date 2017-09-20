package workflows.sra.submission;

import java.util.Calendar;
import java.util.Date;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.stereotype.Service;

import fr.cea.ig.MongoDBDAO;
import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.utils.InstanceConstants;
import play.Logger;

@Service
public class SubmissionWorkflowsHelper {

	
	public void updateSubmissionRelease(Submission submission)
	{
		Study study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, submission.studyCode);
		Calendar calendar = Calendar.getInstance();
		Date date  = calendar.getTime();		
		Date release_date  = calendar.getTime();
		Logger.debug("update submission date study "+study.code+" with date "+release_date);
		MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, 
				DBQuery.is("code", submission.code),
				DBUpdate.set("submissionDate", date));	

			MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
				DBQuery.is("accession", study.accession),
				DBUpdate.set("releaseDate", release_date));
	}
	
	public void removeSubmissionRelease(Submission submission)
	{
		Study study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, submission.studyCode);
		
		MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, 
				DBQuery.is("code", submission.code),
				DBUpdate.set("submissionDate", null));	

			MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
				DBQuery.is("accession", study.accession),
				DBUpdate.set("releaseDate", null));
	}
}