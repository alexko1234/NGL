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

import models.laboratory.run.instance.File;
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

public class FilesTests extends AbstractTests{
	
	
	
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
		
		Random r = new Random();
		int rVal = 1 + r.nextInt(100- 1);
		String readSetCode = "test" + rVal;		
		readsets.add(RunMockHelper.newReadSet(readSetCode)); // like that, we have a unique code !
		lane.readsets = readsets;
		
		Lane lane2 = RunMockHelper.newLane(2);
		List<Lane> lanes = new ArrayList<Lane>();
		lanes.add(lane);
		lanes.add(lane2);
		run.lanes = lanes;
		 
		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		
        assertThat(status(result)).isEqualTo(OK);
        
        File file =  RunMockHelper.newFile("newfiletest");
        List<File> files =  new ArrayList<File>();
        files.add(file);
        run.lanes.get(0).readsets.get(0).files = files; 
		
		result = callAction(controllers.runs.api.routes.ref.Files.save(readSetCode),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
	 	
        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/json");
        assertThat(charset(result)).isEqualTo("utf-8");
	 }
	
	
	 @Test
	 public void testFileUpdate() {
		 Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET2"));
		 String readSetCode = "";
		 if(runDelete==null){ 
			 
			 //code de testFileCreate
			 	Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET2");
				run.dispatch = true; // For the archive test
				Lane lane = RunMockHelper.newLane(1);
				List<ReadSet> readsets =  new ArrayList<ReadSet>();
				
				Random r = new Random();
				int rVal = 1 + r.nextInt(100- 1);
				readSetCode = "test" + rVal;		
				readsets.add(RunMockHelper.newReadSet(readSetCode)); // like that, we have a unique code !
				lane.readsets = readsets;
				
				Lane lane2 = RunMockHelper.newLane(2);
				List<Lane> lanes = new ArrayList<Lane>();
				lanes.add(lane);
				lanes.add(lane2);
				run.lanes = lanes;
				 
				callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
				
		        File file =  RunMockHelper.newFile("newfiletest");
		        List<File> files =  new ArrayList<File>();
		        files.add(file);
		        run.lanes.get(0).readsets.get(0).files = files; 
				
				callAction(controllers.runs.api.routes.ref.Files.save(readSetCode),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
				
		 }
		 else {
			 ReadSet readset = runDelete.lanes.get(0).readsets.get(0); 
			 readSetCode = readset.code;
			 
			 if (readset.files == null || (readset.files.size() == 0)) {
				 
			        File file =  RunMockHelper.newFile("newfiletest");
			        List<File> files =  new ArrayList<File>();
			        files.add(file);
			        runDelete.lanes.get(0).readsets.get(0).files = files; 
					
					callAction(controllers.runs.api.routes.ref.Files.save(readSetCode),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
			 }
		 } 
		 
		  File file = RunMockHelper.newFile("newfiletest");
		  file.extension = "IMG";
	      List<File> files =  new ArrayList<File>();
	      files.add(file);
	      runDelete.lanes.get(0).readsets.get(0).files = files; 
	        
		  Result result = callAction(controllers.runs.api.routes.ref.Files.save(readSetCode),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
		  //System.out.println(contentAsString(result));
	      assertThat(status(result)).isEqualTo(OK);
	      assertThat(contentType(result)).isEqualTo("application/json");
	      assertThat(charset(result)).isEqualTo("utf-8");
	 }
	
	 
	 @Test
	 public void testFileShow() {
		 Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET2"));
		 String readSetCode = "";
		 if(runDelete==null){ 
			 
			//code de testFileCreate
			 	Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET2");
				run.dispatch = true; // For the archive test
				Lane lane = RunMockHelper.newLane(1);
				List<ReadSet> readsets =  new ArrayList<ReadSet>();
				
				Random r = new Random();
				int rVal = 1 + r.nextInt(100- 1);
				readSetCode = "test" + rVal;		
				readsets.add(RunMockHelper.newReadSet(readSetCode)); // like that, we have a unique code !
				lane.readsets = readsets;
				
				Lane lane2 = RunMockHelper.newLane(2);
				List<Lane> lanes = new ArrayList<Lane>();
				lanes.add(lane);
				lanes.add(lane2);
				run.lanes = lanes;
				 
				callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
				
		        File file =  RunMockHelper.newFile("newfiletest");
		        List<File> files =  new ArrayList<File>();
		        files.add(file);
		        run.lanes.get(0).readsets.get(0).files = files; 
				
				callAction(controllers.runs.api.routes.ref.Files.save(readSetCode),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
				
		 }
		 else {
			 ReadSet readset = runDelete.lanes.get(0).readsets.get(0); 
			 readSetCode = readset.code;
			 
			 if (readset.files == null || (readset.files.size() == 0)) {
				 
			        File file =  RunMockHelper.newFile("newfiletest");
			        List<File> files =  new ArrayList<File>();
			        files.add(file);
			        runDelete.lanes.get(0).readsets.get(0).files = files; 
					
					callAction(controllers.runs.api.routes.ref.Files.save(readSetCode),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
			 }
		 }
		 
		 
		 
		  Result result = callAction(controllers.runs.api.routes.ref.Files.get(readSetCode,"newfiletest"),fakeRequest());
		   //  System.out.println(contentAsString(result));
	      assertThat(status(result)).isEqualTo(OK);
	      assertThat(contentType(result)).isEqualTo("application/json");
	      assertThat(charset(result)).isEqualTo("utf-8");
	 }
	 
	 
	 @Test
	 public void testDeleteFile(){
		 Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET2"));
		 String readSetCode = "";
		 if(runDelete==null){ 
			 
			//code de testFileCreate
			 	Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET2");
				run.dispatch = true; // For the archive test
				Lane lane = RunMockHelper.newLane(1);
				List<ReadSet> readsets =  new ArrayList<ReadSet>();
				
				Random r = new Random();
				int rVal = 1 + r.nextInt(100- 1);
				readSetCode = "test" + rVal;		
				readsets.add(RunMockHelper.newReadSet(readSetCode)); // like that, we have a unique code !
				lane.readsets = readsets;
				
				Lane lane2 = RunMockHelper.newLane(2);
				List<Lane> lanes = new ArrayList<Lane>();
				lanes.add(lane);
				lanes.add(lane2);
				run.lanes = lanes;
				 
				callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
				
		        File file =  RunMockHelper.newFile("newfiletest");
		        List<File> files =  new ArrayList<File>();
		        files.add(file);
		        run.lanes.get(0).readsets.get(0).files = files; 
				
				callAction(controllers.runs.api.routes.ref.Files.save(readSetCode),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
				
		 }
		 else {
			 ReadSet readset = runDelete.lanes.get(0).readsets.get(0); 
			 readSetCode = readset.code;
			 
			 if (readset.files == null || (readset.files.size() == 0)) {
				 
			        File file =  RunMockHelper.newFile("newfiletest");
			        List<File> files =  new ArrayList<File>();
			        files.add(file);
			        runDelete.lanes.get(0).readsets.get(0).files = files; 
					
					callAction(controllers.runs.api.routes.ref.Files.save(readSetCode),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
			 }
		 } 
		 
		 Result result = callAction(controllers.runs.api.routes.ref.Files.delete(readSetCode,"newfiletest"),fakeRequest());
         assertThat(status(result)).isEqualTo(OK);
	 }
	 
	 
	 @Test
	 public void testRemoveFiles(){
		 Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET2"));
		 String readSetCode = "";
		 if(runDelete==null){ 
			 
			//code de testFileCreate
			 	Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET2");
				run.dispatch = true; // For the archive test
				Lane lane = RunMockHelper.newLane(1);
				List<ReadSet> readsets =  new ArrayList<ReadSet>();
				
				Random r = new Random();
				int rVal = 1 + r.nextInt(100- 1);
				readSetCode = "test" + rVal;		
				readsets.add(RunMockHelper.newReadSet(readSetCode)); // like that, we have a unique code !
				lane.readsets = readsets;
				
				Lane lane2 = RunMockHelper.newLane(2);
				List<Lane> lanes = new ArrayList<Lane>();
				lanes.add(lane);
				lanes.add(lane2);
				run.lanes = lanes;
				 
				callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
				
		        File file =  RunMockHelper.newFile("newfiletest");
		        List<File> files =  new ArrayList<File>();
		        files.add(file);
		        run.lanes.get(0).readsets.get(0).files = files; 
				
				callAction(controllers.runs.api.routes.ref.Files.save(readSetCode),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
				
		 }
		 else {
			 ReadSet readset = runDelete.lanes.get(0).readsets.get(0); 
			 
			 if (readset.files == null || (readset.files.size() == 0)) {
				 
			        File file =  RunMockHelper.newFile("newfiletest");
			        List<File> files =  new ArrayList<File>();
			        files.add(file);
			        runDelete.lanes.get(0).readsets.get(0).files = files; 
					
					callAction(controllers.runs.api.routes.ref.Files.save(readSetCode),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
			 }
		 }
	 
		 Result result = callAction(controllers.runs.api.routes.ref.Runs.deleteFiles("YANN_TEST1FORREADSET2"),fakeRequest());
		  //Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET2"));
		  // System.out.println(Json.toJson(runDelete).toString());
		  //assertThat(runDelete).isNull();
          assertThat(status(result)).isEqualTo(OK);
	 }
	 

}
