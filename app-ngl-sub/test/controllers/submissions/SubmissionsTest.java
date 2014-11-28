package controllers.submissions;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.io.IOException;
import java.util.List;

import models.sra.experiment.instance.Experiment;
import models.sra.experiment.instance.RawData;
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
import utils.SubmissionMockHelper;
import builder.data.ExperimentBuilder;
import builder.data.RawDataBuilder;
import builder.data.RunBuilder;
import builder.data.StateBuilder;
import builder.data.SubmissionBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.cea.ig.MongoDBDAO;

public class SubmissionsTest extends AbstractTestController{

	
	@BeforeClass
	public static void initData()
	{
		//Create simple submission with state
		//Submission submission = SubmissionMockHelper.newSubmission("code1");
		Submission submission = new SubmissionBuilder()
								.withCode("code1")
								.withState(new StateBuilder().withCode("Scode1").build())
								.build();
		MongoDBDAO.save(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submission);
		
		//Create submission withRawData
		Submission submissionRD = new SubmissionBuilder()
								.withCode("sub2")
								.addExperimentCode("exp1")
								.build();
		MongoDBDAO.save(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submissionRD);
		Experiment experiment = new ExperimentBuilder()
								.withCode("exp1")
								.withRun(new RunBuilder()
											.withCode("run1")
											.addRawData(new RawDataBuilder()
														.withRelatifName("path1").build())
											.addRawData(new RawDataBuilder()
														.withRelatifName("path2").build())
											.build())
								.build();
		MongoDBDAO.save(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, experiment);
	}

	@AfterClass
	public static void deleteData()
	{
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "code1");
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "sub2");
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "exp1");
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
	public void shouldUpdateSubmission()
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
	
	@Test
	public void shouldUpdateSubmissionState()
	{
		Result result = callAction(controllers.submissions.api.routes.ref.Submissions.updateState("code1","Scode1Update"));
		assertThat(status(result)).isEqualTo(OK);
		Submission submissionUpdateted = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "code1");
		Logger.info("submission updated "+submissionUpdateted.state.code);
		assertThat(submissionUpdateted.state.code).isEqualTo("Scode1Update");
	}
	
	@Test
	public void shouldGetRawDatas() throws JsonParseException, JsonMappingException, IOException
	{
		Result result = callAction(controllers.submissions.api.routes.ref.Submissions.getRawDatas("sub2"));
		Logger.info(contentAsString(result));
		assertThat(status(result)).isEqualTo(OK);
		assertThat(contentType(result)).isEqualTo("application/json");
		List object = new ObjectMapper().readValue(contentAsString(result), List.class);
		assertThat(object.size()).isEqualTo(2);
		for(Object o : object)
		{
			Logger.info(o.toString());
			JsonNode jsonNode = Json.toJson(o);
			assertThat(jsonNode.findValue("relatifName")).isNotNull();
		}
		
	}
}
