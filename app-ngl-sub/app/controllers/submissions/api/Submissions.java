package controllers.submissions.api;

import java.util.List;

import models.sra.submission.instance.Submission;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;

import play.libs.Json;
import play.mvc.Result;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class Submissions extends CommonController{

	
	public static Result search(String state)
	{
		MongoDBResult<Submission> results = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.is("state.code", state));
		List<Submission> submissions = results.toList();
		return ok(Json.toJson(submissions));
	}
}
