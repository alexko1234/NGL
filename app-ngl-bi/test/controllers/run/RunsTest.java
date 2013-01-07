package controllers.run;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.description.common.PropertyDefinition;
import models.instance.common.PropertyValue;
import models.instance.common.TraceInformation;
import models.instance.instrument.InstrumentUsed;
import models.instance.run.Lane;
import models.instance.run.ReadSet;
import models.instance.run.Run;
import models.instance.validation.RunPropertyDefinitionHelper;
import net.vz.mongodb.jackson.DBQuery;

import org.codehaus.jackson.JsonNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.Logger;
import play.test.FakeApplication;
import play.test.Helpers;

import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import fr.cea.ig.MongoDBDAO;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;
import play.mvc.Result;
import play.test.TestBrowser;
import utils.AbstractTests;
import utils.RunMockHelper;
import utils.RunMockHelperOld;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class RunsTest extends AbstractTests {
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
	}
	
	@Test
	public void testRuns() {
			Run runDelete = MongoDBDAO.findOne("cng.run.illuminaYann2",Run.class,DBQuery.is("code","YANN_TEST1"));
			if(runDelete!=null){
				MongoDBDAO.delete("cng.run.illuminaYann2", Run.class, runDelete._id);
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
			Run run = MongoDBDAO.findOne("cng.run.illuminaYann2",Run.class,DBQuery.is("code","YANN_TEST1"));
			
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
			Run run = MongoDBDAO.findOne("cng.run.illuminaYann2",Run.class,DBQuery.is("code","YANN_TEST1"));
			
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
			Run run = MongoDBDAO.findOne("cng.run.illuminaYann2",Run.class,DBQuery.is("code","YANN_TEST1"));
			
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

}
