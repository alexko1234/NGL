package controllers.submissions;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.io.IOException;

import models.sra.submission.instance.Submission;
import models.utils.InstanceConstants;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import utils.AbstractTestController;
import utils.StateBuilder;
import utils.SubmissionBuilder;
import utils.SubmissionMockHelper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.cea.ig.MongoDBDAO;

public class SubmissionsTest extends AbstractTestController{

	
	@BeforeClass
	public static void initData()
	{
		//Submission submission = SubmissionMockHelper.newSubmission("code1");
		Submission submission = new SubmissionBuilder()
								.withCode("code1")
								.withState(new StateBuilder().withCode("Scode1").build())
								.build();
		MongoDBDAO.save(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submission);
	}

	@AfterClass
	public static void deleteData()
	{
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "code1");
	}

	@Test
	public void shouldSearchSubmissionByState() throws JsonParseException, JsonMappingException, IOException
	{
		Result result = callAction(controllers.submissions.api.routes.ref.Submissions.search("Scode1"));
		Logger.info(contentAsString(result));
		assertThat(status(result)).isEqualTo(OK);
		assertThat(contentType(result)).isEqualTo("application/json");
	}
	
	@Test
	public void shouldUpdateSubmissionState()
	{
		//Change state of submission
		//Get submission
		Submission submissionToUpdate = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "code1");
		submissionToUpdate.state.code="Scode1Update";
		//TODO pas de méthode de validation a retester avec méthode de validation
		Result result = callAction(controllers.submissions.api.routes.ref.Submissions.update("code1"),fakeRequest().withJsonBody(Json.toJson(submissionToUpdate)));
		assertThat(status(result)).isEqualTo(OK);
		//Check in db submission status
		Submission submissionUpdated = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "code1");
		Logger.info("submission updated "+submissionUpdated.state.code);
		assertThat(submissionUpdated.state.code).isEqualTo("Scode1Update");
	}
}
