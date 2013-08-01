package controllers.run;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.util.ArrayList;
import java.util.List;

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

public class ReadSetsTests extends AbstractTests {
	
	 @Test
	 public void testReasetCreate() {
		Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET"));
		if(runDelete!=null){
			MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
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
		 
		callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		
		readset = RunMockHelper.newReadSet("ReadSetTEST");
		
		Result result = callAction(controllers.runs.api.routes.ref.ReadSets.save("YANN_TEST1FORREADSET",1),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
		     
	 	//System.out.println(contentAsString(result));
        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/json");
        assertThat(charset(result)).isEqualTo("utf-8");
	 }
	 
	 @Test
	 public void testReasetAdd() {
				
		ReadSet readset2 = RunMockHelper.newReadSet("ReadSetBasicWithRun2");
		ReadSet readset3 = RunMockHelper.newReadSet("ReadSetBasicWithRun3");
		
		
		/*
		run.lanes.get(0).readsets = readsets;
		run.lanes.get(1).readsets = readsets2;
		*/ 
		
		
		Result result = callAction(controllers.runs.api.routes.ref.ReadSets.save("YANN_TEST1FORREADSET",2),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset2)));
		assertThat(status(result)).isEqualTo(OK);
	    assertThat(contentType(result)).isEqualTo("application/json");
	    assertThat(charset(result)).isEqualTo("utf-8");
		result = callAction(controllers.runs.api.routes.ref.ReadSets.save("YANN_TEST1FORREADSET",2),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset3)));
	       
	 	//System.out.println(contentAsString(result));
       assertThat(status(result)).isEqualTo(OK);
       assertThat(contentType(result)).isEqualTo("application/json");
       assertThat(charset(result)).isEqualTo("utf-8");
	 }
	
	
	 @Test	 
	 public void testReasetUpdate() {
		ReadSet readset = RunMockHelper.newReadSet("ReadSetTEST");
		readset.sampleCode = "THE SAMPLE CODE AFTER UPDATE";

		 Result result = callAction(controllers.runs.api.routes.ref.ReadSets.update("ReadSetTEST"),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
	     
	     assertThat(status(result)).isEqualTo(OK);
	     assertThat(contentType(result)).isEqualTo("application/json");
	     assertThat(charset(result)).isEqualTo("utf-8");
	 }
	 
	 @Test	 
	 public void testReasetUpdateWithCreateOrUpdateFunction() {
		 	testReasetCreate() ;
		 
		 
			ReadSet readset = RunMockHelper.newReadSet("ReadSetTEST");
			
			Result result = callAction(controllers.runs.api.routes.ref.ReadSets.save("YANN_TEST1FORREADSET",2),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
		        
		 	//System.out.println(contentAsString(result));
	        assertThat(status(result)).isEqualTo(BAD_REQUEST);
	        assertThat(contentType(result)).isEqualTo("application/json");
	        assertThat(charset(result)).isEqualTo("utf-8");
	 }
	 
	 @Test
	 public void testArchiveReadSet(){
		 Result result = callAction(controllers.archives.api.routes.ref.ReadSets.save("ReadSetTEST"),fakeRequest().withJsonBody(RunMockHelper.getArchiveJson("codeTestArchive")));
         assertThat(status(result)).isEqualTo(OK);
    	 
         result = callAction(controllers.archives.api.routes.ref.ReadSets.save("ReadSetTESTNOTEXIT"),fakeRequest().withJsonBody(RunMockHelper.getArchiveJson("codeTestArchive")));
         assertThat(status(result)).isEqualTo(NOT_FOUND);
	 }
	 
	 @Test
	 public void testNeedAchive(){
		 Result result = callAction(controllers.archives.api.routes.ref.ReadSets.list(),fakeRequest());
         assertThat(status(result)).isEqualTo(OK);
         assertThat(contentType(result)).isEqualTo("application/json");
      	 assertThat(charset(result)).isEqualTo("utf-8");
      	 assertThat(contentAsString(result)).isNotEqualTo("[]").contains("ReadSetBasicWithRun").doesNotContain("ReadSetTEST");
	 }
	 
	 @Test
	 public void testRemoveReadsets(){
		 Result result = callAction(controllers.runs.api.routes.ref.Runs.deleteReadsets("YANN_TEST1FORREADSET"),fakeRequest());
         //System.out.println("RUN WITHOUT READSET: "+runDelete.lanes.get(0).readsets.toArray());
         //assertThat(runDelete).isNull();
         assertThat(status(result)).isEqualTo(OK);
         
	 }
	 
	 
	 
	@Test
	public void testDeleteReadset(){
		testReasetCreate();
		Result result = callAction(controllers.runs.api.routes.ref.ReadSets.delete("ReadSetTEST"),fakeRequest());
		assertThat(status(result)).isEqualTo(OK);	
	}
		

}
