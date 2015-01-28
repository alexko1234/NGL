package controllers.submissions.api;

import models.sra.submit.common.instance.Submission;
import models.utils.InstanceConstants;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class SubmissionsController extends CommonController{

	protected static Submission getSubmission(String code)
	{
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, code);
		return submission;
	}
}
