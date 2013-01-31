package controllers.run;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import net.vz.mongodb.jackson.DBQuery;

import org.junit.Test;

import play.mvc.Result;
import utils.AbstractTests;
import utils.RunMockHelper;
import fr.cea.ig.MongoDBDAO;

public class RunsTest extends AbstractTests {
	
	@Test
	public void testRuns() {
			Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
			if(runDelete!=null){
				System.out.println("EXIST");
				MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}else {
				System.out.println("NOTEXIST");
				
			}
		
			Run run = RunMockHelper.newRun("YANN_TEST1");
			Lane lane = RunMockHelper.newLane(1);
			List<Lane> lanes = new ArrayList<Lane>();
			lanes.add(lane);
			run.lanes = lanes;
		
			ReadSet readset = RunMockHelper.newReadSet("ReadSet1");
			List<ReadSet> readsets = new ArrayList<ReadSet>();
			readsets.add(readset);
			run.lanes.get(0).readsets = readsets;
			
		 	Result result = callAction(controllers.run.routes.ref.Runs.createOrUpdate("json"),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	        
		 	//assertThat(contentAsString(result).)
		 	//System.out.println(contentAsString(result));
	        assertThat(status(result)).isEqualTo(OK);
	        assertThat(contentType(result)).isEqualTo("application/json");
	        assertThat(charset(result)).isEqualTo("utf-8");

    }
	
	@Test
	public void testRunsUpdate() {
			Run run = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
			
			run.dispatch=true;
			
		 	Result result = callAction(controllers.run.routes.ref.Runs.createOrUpdate("json"),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	        
		 	//assertThat(contentAsString(result).)
		 	//System.out.println(contentAsString(result));
	        assertThat(status(result)).isEqualTo(OK);
	        assertThat(contentType(result)).isEqualTo("application/json");
	        assertThat(charset(result)).isEqualTo("utf-8");
	}
	
	@Test
	public void testRunsUpdateWithSameReadSet() {
			Run run = RunMockHelper.newRun("YANN_TEST2");
			Lane lane = RunMockHelper.newLane(1);
			List<Lane> lanes = new ArrayList<Lane>();
			lanes.add(lane);
			run.lanes = lanes;
		
			ReadSet readset = RunMockHelper.newReadSet("ReadSet1");
			List<ReadSet> readsets = new ArrayList<ReadSet>();
			readsets.add(readset);
			run.lanes.get(0).readsets = readsets;
		 	Result result = callAction(controllers.run.routes.ref.Runs.createOrUpdate("json"),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	        
		 	//assertThat(contentAsString(result).)
		 	//System.out.println(contentAsString(result));
	        assertThat(status(result)).isEqualTo(play.mvc.Http.Status.BAD_REQUEST);
	        assertThat(contentType(result)).isEqualTo("application/json");
	        assertThat(charset(result)).isEqualTo("utf-8");
	}
	
	@Test
	public void testLanesUpdateByRun() {
			Run run = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
			
			run.lanes.get(0).properties.remove("nbCycleRead1");
			run.lanes.get(0).properties.put("nbCycleRead1",new PropertyValue("42"));
			
			Result result = callAction(controllers.run.routes.ref.Runs.createOrUpdate("json"),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	        
		 	//assertThat(contentAsString(result).)
		 	//System.out.println(contentAsString(result));
	        assertThat(status(result)).isEqualTo(OK);
	        assertThat(contentType(result)).isEqualTo("application/json");
	        assertThat(contentAsString(result).contains("NewPropertyTestUpdateLane"));
	        assertThat(charset(result)).isEqualTo("utf-8");
	}
	
	@Test
	public void testReadSetUpdateByRun() {
			Run run = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
			
			run.lanes.get(0).readsets.get(0).properties.remove("score");
			run.lanes.get(0).readsets.get(0).properties.put("score",  new PropertyValue("42"));
			Result result = callAction(controllers.run.routes.ref.Runs.createOrUpdate("json"),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	        
		 	//assertThat(contentAsString(result).)
		 	//System.out.println(contentAsString(result));
	        assertThat(status(result)).isEqualTo(OK);
	        assertThat(contentType(result)).isEqualTo("application/json");
	        assertThat(contentAsString(result).contains("NewPropertyTestUpdateReadSet"));
	        assertThat(charset(result)).isEqualTo("utf-8");
	}
	
	@Test
	public void testDeleteRun(){
		Result result = callAction(controllers.run.routes.ref.Runs.remove("YANN_TEST1","json"),fakeRequest());
		assertThat(status(result)).isEqualTo(OK);
		
	}


}
