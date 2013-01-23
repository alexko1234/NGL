package controllers.run;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import net.vz.mongodb.jackson.DBQuery;

import static play.test.Helpers.contentAsString;

import org.junit.Test;

import play.mvc.Result;
import utils.AbstractTests;
import utils.RunMockHelper;
import controllers.run.Files;
import fr.cea.ig.MongoDBDAO;

public class FilesTest extends AbstractTests{

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	@Test
	 public void testFileCreate() {
		Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET2"));
		if(runDelete!=null){
			MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
	
		Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET2");
		run.dispatch = true; // For the archive test
		Lane lane = RunMockHelper.newLane(1);
		List<ReadSet> readsets =  new ArrayList<ReadSet>();
		readsets.add(RunMockHelper.newReadSet("test1"));
		lane.readsets = readsets;
		
		Lane lane2 = RunMockHelper.newLane(2);
		List<Lane> lanes = new ArrayList<Lane>();
		lanes.add(lane);
		lanes.add(lane2);
		run.lanes = lanes;
		 
		Result result = callAction(controllers.run.routes.ref.Runs.createOrUpdate("json"),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		System.out.println(contentAsString(result));
		
		result = callAction(controllers.run.routes.ref.Files.createOrUpdate("test1","json"),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(RunMockHelper.newFile("newfiletest"))));
	 	//System.out.println(contentAsString(result));
       assertThat(status(result)).isEqualTo(OK);
       assertThat(contentType(result)).isEqualTo("application/json");
       assertThat(charset(result)).isEqualTo("utf-8");
	 }
	
	 @Test
	 public void testFileUpdate() {
		File file = RunMockHelper.newFile("newfiletest");
		file.extension = "IMG";
	  Result result = callAction(controllers.run.routes.ref.Files.createOrUpdate("test1","json"),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
	  System.out.println(contentAsString(result));
      assertThat(status(result)).isEqualTo(OK);
      assertThat(contentType(result)).isEqualTo("application/json");
      assertThat(charset(result)).isEqualTo("utf-8");
	 }
	 
	 @Test
	 public void testFileShow() {
	  Result result = callAction(controllers.run.routes.ref.Files.show("test1","testfile","json"));
	  System.out.println(contentAsString(result));
      assertThat(status(result)).isEqualTo(OK);
      assertThat(contentType(result)).isEqualTo("application/json");
      assertThat(charset(result)).isEqualTo("utf-8");
	 }
	
}
