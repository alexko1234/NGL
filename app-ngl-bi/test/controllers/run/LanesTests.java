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
	public void testLanes(){
		Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORLANES"));
		if(runDelete!=null){
			MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
	
		Run run = RunMockHelper.newRun("YANN_TEST1FORLANES");
		Lane lane = RunMockHelper.newLane(1);
		List<Lane> lanes = new ArrayList<Lane>();
		lanes.add(lane);
		run.lanes = lanes;
		
	 	callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
        
	 	lane.abort = TBoolean.TRUE;
	 	
	 	Result result = callAction(controllers.runs.api.routes.ref.Lanes.save("YANN_TEST1FORLANES"),fakeRequest().withJsonBody(RunMockHelper.getJsonLane(lane)));
	 	
		//System.out.println(contentAsString(result));
	    assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/json");
        assertThat(charset(result)).isEqualTo("utf-8");
	}
	 
	 @Test
	 public void testLanesWithRunWithoutLane(){
			Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORLANES"));
			if(runDelete!=null){
				MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
		
			Run run = RunMockHelper.newRun("YANN_TEST1FORLANES");
			Lane lane = RunMockHelper.newLane(1);
		
		 	callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	        
		 	
		 	Result result = callAction(controllers.runs.api.routes.ref.Lanes.save("YANN_TEST1FORLANES"),fakeRequest().withJsonBody(RunMockHelper.getJsonLane(lane)));
		 	
			//System.out.println(contentAsString(result));
		    assertThat(status(result)).isEqualTo(OK);
	        assertThat(contentType(result)).isEqualTo("application/json");
	        assertThat(charset(result)).isEqualTo("utf-8");
		}
	 
	 
	 @Test
	 public void testLanesOnRun(){
		
			Lane lane = RunMockHelper.newLane(2);
			ReadSet readset = RunMockHelper.newReadSet("ReadSetLanes2");
			List<ReadSet> readsets = new ArrayList<ReadSet>();
			readsets.add(readset);

			lane.readsets = readsets;
		 	
		 	Result result = callAction(controllers.runs.api.routes.ref.Lanes.save("YANN_TEST1FORLANES"),fakeRequest().withJsonBody(RunMockHelper.getJsonLane(lane)));
		 	
		 	//System.out.println(contentAsString(result));
		    assertThat(status(result)).isEqualTo(OK);
	        assertThat(contentType(result)).isEqualTo("application/json");
	        assertThat(charset(result)).isEqualTo("utf-8");
	
		}
	 
	 
	 @Test
	public void testLanesOnRunSameReadSet(){
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


}
