package controllers.run;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.instance.run.Lane;
import models.instance.run.ReadSet;
import models.instance.run.Run;
import net.vz.mongodb.jackson.DBQuery;

import org.junit.Test;

import fr.cea.ig.MongoDBDAO;
import play.mvc.Result;
import utils.AbstractTests;
import utils.RunMockHelper;
import utils.RunMockHelperOld;


import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class ReadSetsTests extends AbstractTests {

	public Map<String,String> fakeConfiguration(){
		Map<String,String> config = new HashMap<String,String>();
		config.put("mongodb.database", "NGL-BI");
		config.put("mongodb.credentials", "ngl-bi:NglBiPassW");
		config.put("mongodb.servers", "gsphere.genoscope.cns.fr:27017");
		return config;
		
	}
	
	
	
	@Test
	 public void testReasetCreate() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		Run runDelete = MongoDBDAO.findOne("cng.run.illuminaYann2",Run.class,DBQuery.is("code","YANN_TEST1FORREADSET"));
		if(runDelete!=null){
			MongoDBDAO.delete("cng.run.illuminaYann2", Run.class, runDelete._id);
		}
	
		Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET");
		run.dispatch = true; // For the archive test
		Lane lane = RunMockHelper.newLane(1);
		Lane lane2 = RunMockHelper.newLane(2);
		List<Lane> lanes = new ArrayList<Lane>();
		lanes.add(lane);
		lanes.add(lane2);
		run.lanes = lanes;
	
		ReadSet readset = RunMockHelper.newReadSet("ReadSetBasicWithRun");
		ReadSet readset2 = RunMockHelper.newReadSet("ReadSetBasicWithRun2");
		List<ReadSet> readsets = new ArrayList<ReadSet>();
		List<ReadSet> readsets2 = new ArrayList<ReadSet>();
		readsets.add(readset);
		readsets2.add(readset2);
		
		
		run.lanes.get(0).readsets = readsets;
		run.lanes.get(1).readsets = readsets2;
		 
		 
		callAction(controllers.run.routes.ref.Runs.createOrUpdate("json"),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		
		readset = RunMockHelper.newReadSet("ReadSetTEST");
		
		Result result = callAction(controllers.run.routes.ref.ReadSets.createOrUpdate("YANN_TEST1FORREADSET",1,"json"),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
	        
	 	//System.out.println(contentAsString(result));
        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/json");
        assertThat(charset(result)).isEqualTo("utf-8");
		       }});
	 }
	 
	 @Test	 
	 public void testReasetUpdate() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		ReadSet readset = RunMockHelper.newReadSet("ReadSetTEST");
		readset.sampleCode = "THE SAMPLE CODE AFTER UPDATE";

		 Result result = callAction(controllers.run.routes.ref.ReadSets.update("ReadSetTEST","json"),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
	     
	     assertThat(status(result)).isEqualTo(OK);
	     assertThat(contentType(result)).isEqualTo("application/json");
	     assertThat(charset(result)).isEqualTo("utf-8");
		       }});
	 }
	 
	 @Test	 
	 public void testReasetUpdateWithCreateOrUpdateFunction() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			ReadSet readset = RunMockHelper.newReadSet("ReadSetTEST");
			
			Result result = callAction(controllers.run.routes.ref.ReadSets.createOrUpdate("YANN_TEST1FORREADSET",2,"json"),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
		        
		 	//System.out.println(contentAsString(result));
	        assertThat(status(result)).isEqualTo(BAD_REQUEST);
	        assertThat(contentType(result)).isEqualTo("application/json");
	        assertThat(charset(result)).isEqualTo("utf-8");
		       }});
	 }
	 
	 @Test
	 public void testArchiveReadSet(){
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		 Result result = callAction(controllers.run.routes.ref.ReadSets.updateArchive("ReadSetTEST","json"),fakeRequest().withJsonBody(RunMockHelperOld.getArchiveJson("codeTestArchive")));
         assertThat(status(result)).isEqualTo(OK);
    	 
         result = callAction(controllers.run.routes.ref.ReadSets.updateArchive("ReadSetTESTNOTEXIT","json"),fakeRequest().withJsonBody(RunMockHelperOld.getArchiveJson("codeTestArchive")));
         assertThat(status(result)).isEqualTo(NOT_FOUND);
		       }});
	 }
	 
	 @Test
	 public void testNeedAchive(){
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		 Result result = callAction(controllers.run.routes.ref.ReadSets.needArchive("json"),fakeRequest());
         assertThat(status(result)).isEqualTo(OK);
         assertThat(contentType(result)).isEqualTo("application/json");
      	 assertThat(charset(result)).isEqualTo("utf-8");
      	 assertThat(contentAsString(result)).isNotEqualTo("[]").contains("ReadSetBasicWithRun").doesNotContain("ReadSetTEST");
		       }});
	 }

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	 
	

}
