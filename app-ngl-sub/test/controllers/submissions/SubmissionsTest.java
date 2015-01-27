package controllers.submissions;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import models.sra.experiment.instance.Experiment;
import models.sra.sample.instance.Sample;
import models.sra.study.instance.Study;
import models.sra.submission.instance.Submission;
import models.utils.InstanceConstants;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import utils.AbstractTestController;
import builder.data.ExperimentBuilder;
import builder.data.RawDataBuilder;
import builder.data.RunBuilder;
import builder.data.SampleBuilder;
import builder.data.StateBuilder;
import builder.data.StudyBuilder;
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
		
		//Create complete submission withRawData and with information for createXML
		Submission submissionRD = new SubmissionBuilder()
								.withCode("sub2")
								.withState(new StateBuilder().withCode("state1").withUser("ejacoby@genoscope.cns.fr").build())
								.withSubmissionDirectory(System.getProperty("user.home")+"/NGL-SUB-Test")
								.withStudyCode("study1")
								.addExperimentCode("exp1")
								.addSampleCode("samp1")
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
		Sample sample = new SampleBuilder()
						.withCode("samp1")
						.build();
		MongoDBDAO.save(InstanceConstants.SRA_SAMPLE_COLL_NAME, sample);
		Study study = new StudyBuilder()
					.withCode("study1")
					.build();
		MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);
		
		
	}

	@AfterClass
	public static void deleteData()
	{
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "code1");
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "sub2");
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "exp1");
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, "samp1");
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, "study1");
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
		submissionToUpdate.state.code="IN_WAITING";
		//TODO pas de méthode de validation a retester avec méthode de validation
		Result result = callAction(controllers.submissions.api.routes.ref.Submissions.update("code1"),fakeRequest().withJsonBody(Json.toJson(submissionToUpdate)));
		Logger.info(contentAsString(result));
		assertThat(status(result)).isEqualTo(OK);
		//Check in db submission status
		Submission submissionUpdated = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "code1");
		Logger.info("submission updated "+submissionUpdated.state.code);
		assertThat(submissionUpdated.state.code).isEqualTo("IN_WAITING");
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
	
	@Test
	public void shouldCreateXML()
	{
		Result result = callAction(controllers.submissions.api.routes.ref.Submissions.createXml("sub2"));
		Logger.info(contentAsString(result));
	}
	
	@Test
	public void shouldTreatmentAC() throws ClientProtocolException, IOException
	{
		
		File ebiFileAc = new File(System.getProperty("user.home")+"/NGL-SUB-Test/RESULT_AC");
		
		Result result = callAction(controllers.submissions.api.routes.ref.Submissions.treatmentAc("sub2"),fakeRequest().withJsonBody(Json.toJson(ebiFileAc)));
		Logger.debug("Result "+result);
		assertThat(status(result)).isEqualTo(OK);
		Submission submissionSubmited = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "sub2");
		Logger.info("submission submited "+submissionSubmited);
		assertThat(submissionSubmited.state.code).isEqualTo("submitted");
	}
}
