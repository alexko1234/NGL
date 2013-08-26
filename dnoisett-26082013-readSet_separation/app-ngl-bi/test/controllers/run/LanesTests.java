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
import java.util.Random;

import models.laboratory.common.instance.TBoolean;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import net.vz.mongodb.jackson.DBQuery;

import org.junit.Test;

import play.mvc.Result;
import utils.AbstractTests;
import utils.RunMockHelper;
import controllers.Constants;
import fr.cea.ig.MongoDBDAO;

public class LanesTests extends AbstractTests {
	
	@Test
	public void testNewLaneOnNewRun(){
		Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORLANES"));
		if(runDelete!=null){
			MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
	
		Run run = RunMockHelper.newRun("YANN_TEST1FORLANES");
	
	 	callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	 	
		Random r = new Random();
		int rVal = 3 + r.nextInt(97);
		
		Lane lane = RunMockHelper.newLane(rVal);
		List<Lane> lanes = new ArrayList<Lane>();
		lanes.add(lane);
		run.lanes = lanes;
	 	lane.abort = TBoolean.TRUE;
	 	
	 	Result result = callAction(controllers.runs.api.routes.ref.Lanes.save("YANN_TEST1FORLANES"),fakeRequest().withJsonBody(RunMockHelper.getJsonLane(lane)));
	 	
		//System.out.println(contentAsString(result));
	    assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/json");
        assertThat(charset(result)).isEqualTo("utf-8");
	}
	 
	 @Test
	 public void testNewLaneWithRunNotAssociated(){
			Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORLANES"));
			if(runDelete!=null){
				MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
		
			Run run = RunMockHelper.newRun("YANN_TEST1FORLANES");
		 	callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	        
		 	Lane lane = RunMockHelper.newLane(1);
		 	Result result = callAction(controllers.runs.api.routes.ref.Lanes.save("YANN_TEST1FORLANES"),fakeRequest().withJsonBody(RunMockHelper.getJsonLane(lane)));
		 	
			//System.out.println(contentAsString(result));
		    assertThat(status(result)).isEqualTo(OK);
	        assertThat(contentType(result)).isEqualTo("application/json");
	        assertThat(charset(result)).isEqualTo("utf-8");
		}
	 
	 
	 @Test
	 public void testNewLaneWithNewReadSetOnTheRun(){
		 
			Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORLANES"));
			if(runDelete!=null){
				MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
		
			Lane lane = RunMockHelper.newLane(2);
			ReadSet readset = RunMockHelper.newReadSet("ReadSetLanes2");
			List<ReadSet> readsets = new ArrayList<ReadSet>();
			readsets.add(readset);
			lane.readsets = readsets;
		 	
		 	Result result = callAction(controllers.runs.api.routes.ref.Lanes.save("YANN_TEST1FORLANES"),fakeRequest().withJsonBody(RunMockHelper.getJsonLane(lane)));
		 	
		 	//System.out.println(contentAsString(result));
		    assertThat(status(result)).isEqualTo(play.mvc.Http.Status.NOT_FOUND);
		}
	 
	 
	@Test
	public void testLaneOnRunWithSameReadSet(){
		Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORLANES"));
		if(runDelete!=null){
			MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
	
		Run run = RunMockHelper.newRun("YANN_TEST1FORLANES");
		Lane lane = RunMockHelper.newLane(1);
		ReadSet readset = RunMockHelper.newReadSet("ReadSetLanes");
		List<ReadSet> readsets = new ArrayList<ReadSet>();
		readsets.add(readset);
		lane.readsets = readsets;
		
	 	callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	 	
		lane = RunMockHelper.newLane(1);
		lane.readsets = readsets;
	 	
	 	Result result = callAction(controllers.runs.api.routes.ref.Lanes.save("YANN_TEST1FORLANES"),fakeRequest().withJsonBody(RunMockHelper.getJsonLane(lane)));
	 	
	 	//System.out.println(contentAsString(result));
	    assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/json");
        assertThat(charset(result)).isEqualTo("utf-8");
	}
	 
	 
	@Test
	public void testLanesOnRunWithSameReadSet(){
		Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORLANES"));
		if(runDelete!=null){
			MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
	
		Run run = RunMockHelper.newRun("YANN_TEST1FORLANES");
		Lane lane = RunMockHelper.newLane(1);
		List<Lane> lanes = new ArrayList<Lane>();
		lanes.add(lane);
		run.lanes = lanes;
		ReadSet readset = RunMockHelper.newReadSet("ReadSetLanes");
		List<ReadSet> readsets = new ArrayList<ReadSet>();
		readsets.add(readset);
		lane.readsets = readsets;
		
	 	callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	 	
		lane = RunMockHelper.newLane(2);
		lane.readsets = readsets;
	 	
	 	Result result = callAction(controllers.runs.api.routes.ref.Lanes.save("YANN_TEST1FORLANES"),fakeRequest().withJsonBody(RunMockHelper.getJsonLane(lane)));
	 	
	 	//System.out.println(contentAsString(result));
	    assertThat(status(result)).isEqualTo(play.mvc.Http.Status.BAD_REQUEST);
        assertThat(contentType(result)).isEqualTo("application/json");
        assertThat(charset(result)).isEqualTo("utf-8");
	} 


}
