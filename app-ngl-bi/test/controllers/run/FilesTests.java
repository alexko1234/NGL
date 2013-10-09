package controllers.run;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;

import org.junit.Test;

import play.Logger;
import play.mvc.Result;
import utils.AbstractTests;
import utils.RunMockHelper;
import fr.cea.ig.MongoDBDAO;

public class FilesTests extends AbstractTests{
	
	@Test
	 public void testFileCreate() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		     public void run() {
		Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET2"));
		if(runDelete!=null){
			MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
		ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
		if(readSetDelete!=null){
			MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
		}
		ReadSet ReadSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("fullname","newfiletest"));
		if(ReadSet!=null){
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, DBQuery.is("code","rdCode"), DBUpdate.unset("files"));
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, DBQuery.is("code","rdCode"), DBUpdate.pull("files",null));	
		}
	
		Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET2");
		run.dispatch = true; 

		
		ReadSet rd = RunMockHelper.newReadSet("rdCode"); 
		rd.runCode = run.code;
		
		Lane lane = RunMockHelper.newLane(1);
		lane.readSetCodes = null;
		
		Lane lane2 = RunMockHelper.newLane(2);
		List<Lane> lanes = new ArrayList<Lane>();
		lanes.add(lane);
		lanes.add(lane2);
		run.lanes = lanes;
		 
		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
        assertThat(status(result)).isEqualTo(OK);
        
        result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(rd)));
        assertThat(status(result)).isEqualTo(OK);
        
        File file =  RunMockHelper.newFile("newfiletest");
        List<File> files =  new ArrayList<File>();
        files.add(file);
        rd.files = files; 
		
		result = callAction(controllers.readsets.api.routes.ref.Files.save("rdCode"),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
	 	
        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/json");
        assertThat(charset(result)).isEqualTo("utf-8");
        
	    //query for control
        ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("files.fullname",file.fullname));
        assertThat(readSet.files.size()).isEqualTo(1);
        assertThat(readSet.files.get(0).fullname).isEqualTo(file.fullname);
        
        
				}});
	 }
	
	
	@Test
	 public void testFileExtensionUpdate() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		     public void run() {

			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET2"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
			ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
			if(readSetDelete!=null){
				MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
			}
			ReadSet fileDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("fullname","newfiletest"));
			if(fileDelete!=null){
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, DBQuery.is("code","rdCode"), DBUpdate.unset("files"));
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, DBQuery.is("code","rdCode"), DBUpdate.pull("files",null));	
			}
			
			Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET2");
			run.dispatch = true; // For the archive test
			Lane lane = RunMockHelper.newLane(1);
			lane.readSetCodes = null;	
			
			ReadSet rd = RunMockHelper.newReadSet("rdCode");
			rd.runCode = run.code;
			
			Lane lane2 = RunMockHelper.newLane(2);
			List<Lane> lanes = new ArrayList<Lane>();
			lanes.add(lane);
			lanes.add(lane2);
			run.lanes = lanes;
			 
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	        assertThat(status(result)).isEqualTo(OK);
	        
	        result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(rd)));
	        assertThat(status(result)).isEqualTo(OK);
	        
	        File file =  RunMockHelper.newFile("newfiletest");
	        file.extension = "DOC";
	        List<File> files =  new ArrayList<File>();
	        files.add(file);
	        rd.files = files; 
			
			result = callAction(controllers.readsets.api.routes.ref.Files.save("rdCode"),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
		 	
	        assertThat(status(result)).isEqualTo(OK);
	        assertThat(contentType(result)).isEqualTo("application/json");
	        assertThat(charset(result)).isEqualTo("utf-8");
	        
		  file.extension = "IMG";

		  	result = callAction(controllers.readsets.api.routes.ref.Files.update("rdCode", "newfiletest"),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
	      assertThat(status(result)).isEqualTo(OK);
	      assertThat(contentType(result)).isEqualTo("application/json");
	      assertThat(charset(result)).isEqualTo("utf-8");
	      
		    //query for control
	        ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("files.fullname",file.fullname));
	        assertThat(readSet.files.size()).isEqualTo(1);
	        assertThat(readSet.files.get(0).extension).isEqualTo(file.extension );
	        
	      
				}});
	 }
	
	 
	 @Test
	 public void testFileShow() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		     public void run() {

			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET2"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
			ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
			if(readSetDelete!=null){
				MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
			}
			ReadSet fileDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("fullname","newfiletest"));
			if(fileDelete!=null){
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, DBQuery.is("code","rdCode"), DBUpdate.unset("files"));
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, DBQuery.is("code","rdCode"), DBUpdate.pull("files",null));	
			}
		
			Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET2");
			run.dispatch = true; // For the archive test
			Lane lane = RunMockHelper.newLane(1);
			lane.readSetCodes = null;	
			
			ReadSet rd = RunMockHelper.newReadSet("rdCode"); 
			rd.runCode = run.code;
			
			Lane lane2 = RunMockHelper.newLane(2);
			List<Lane> lanes = new ArrayList<Lane>();
			lanes.add(lane);
			lanes.add(lane2);
			run.lanes = lanes;
			 
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	        assertThat(status(result)).isEqualTo(OK);
	        
	        result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(rd)));
	        assertThat(status(result)).isEqualTo(OK);
	        
	        File file =  RunMockHelper.newFile("newfiletest");
	        List<File> files =  new ArrayList<File>();
	        files.add(file);
	        rd.files = files; 
			
			result = callAction(controllers.readsets.api.routes.ref.Files.save("rdCode"),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
		 	
	        assertThat(status(result)).isEqualTo(OK);
	        assertThat(contentType(result)).isEqualTo("application/json");
	        assertThat(charset(result)).isEqualTo("utf-8");
		 
		 
		    // specific code
			  result = callAction(controllers.readsets.api.routes.ref.Files.get("rdCode","newfiletest"),fakeRequest());
		      assertThat(status(result)).isEqualTo(OK);
		      assertThat(contentType(result)).isEqualTo("application/json");
		      assertThat(charset(result)).isEqualTo("utf-8");
		      
				}});
	 }
	 
	 
	 @Test
	 public void testDeleteFile(){
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		     public void run() {
	
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET2"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
			ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
			if(readSetDelete!=null){
				MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
			}
			ReadSet fileDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("fullname","newfiletest"));
			if(fileDelete!=null){
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, DBQuery.is("code","rdCode"), DBUpdate.unset("files"));
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, DBQuery.is("code","rdCode"), DBUpdate.pull("files",null));	
			}
		
			Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET2");
			run.dispatch = true; // For the archive test
			Lane lane = RunMockHelper.newLane(1);
			lane.readSetCodes = null;	
			
			ReadSet rd = RunMockHelper.newReadSet("rdCode");
			rd.runCode = run.code;
			
			Lane lane2 = RunMockHelper.newLane(2);
			List<Lane> lanes = new ArrayList<Lane>();
			lanes.add(lane);
			lanes.add(lane2);
			run.lanes = lanes;
			 
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	        assertThat(status(result)).isEqualTo(OK);
	        
	        result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(rd)));
	        assertThat(status(result)).isEqualTo(OK);
	        
	        File file =  RunMockHelper.newFile("newfiletest");
	        List<File> files =  new ArrayList<File>();
	        files.add(file);
	        rd.files = files; 
			
			result = callAction(controllers.readsets.api.routes.ref.Files.save("rdCode"),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
		 	
	        assertThat(status(result)).isEqualTo(OK);
	        assertThat(contentType(result)).isEqualTo("application/json");
	        assertThat(charset(result)).isEqualTo("utf-8");
		 
	        // specific code
			 result = callAction(controllers.readsets.api.routes.ref.Files.delete("rdCode","newfiletest"),fakeRequest());
	         assertThat(status(result)).isEqualTo(OK);
	         
	         
	 	    //query for control
	         ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,rd.code);
	         assertThat(readSet.files.size()).isEqualTo(0);
	         
	         
				}});
	 }
	 
	 
	 @Test
	 public void testRemoveFiles(){
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		     public void run() {
	 
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET2"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
			ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
			if(readSetDelete!=null){
				MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
			}
			ReadSet fileDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("fullname","newfiletest"));
			if(fileDelete!=null){
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, DBQuery.is("code","rdCode"), DBUpdate.unset("files"));
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, DBQuery.is("code","rdCode"), DBUpdate.pull("files",null));	
			}
		
			Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET2");
			run.dispatch = true; // For the archive test
			Lane lane = RunMockHelper.newLane(1);
			lane.readSetCodes = null;
			
			ReadSet rd = RunMockHelper.newReadSet("rdCode");
			rd.runCode = run.code;
			
			Lane lane2 = RunMockHelper.newLane(2);
			List<Lane> lanes = new ArrayList<Lane>();
			lanes.add(lane);
			lanes.add(lane2);
			run.lanes = lanes;
			 
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	        assertThat(status(result)).isEqualTo(OK);
	        
	        result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(rd)));
	        assertThat(status(result)).isEqualTo(OK);
	        
	        File file =  RunMockHelper.newFile("newfiletest");
	        File file2 =  RunMockHelper.newFile("newfiletest2");
	        List<File> files =  new ArrayList<File>();
	        files.add(file);
	        files.add(file2);
	        rd.files = files; 
			
			result = callAction(controllers.readsets.api.routes.ref.Files.save("rdCode"),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file)));
	        assertThat(status(result)).isEqualTo(OK);
	        
			result = callAction(controllers.readsets.api.routes.ref.Files.save("rdCode"),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(file2)));
	        assertThat(status(result)).isEqualTo(OK);
	 
		     // specific code
			 result = callAction(controllers.readsets.api.routes.ref.Files.deleteByRunCode("YANN_TEST1FORREADSET2"),fakeRequest());
	         assertThat(status(result)).isEqualTo(OK);
	         
		 	    //query for control
	         ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,rd.code);
	         assertThat(readSet.files).isNull();
	         
				}});
	 }
	 
}
