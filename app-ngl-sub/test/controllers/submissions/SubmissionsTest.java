package controllers.submissions;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.status;

import java.io.IOException;

import models.sra.submission.instance.Submission;
import models.utils.InstanceConstants;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Logger;
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
}
