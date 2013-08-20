package models.instances.validation;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.InstrumentUsed;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;

import org.junit.Test;

import play.data.validation.ValidationError;
import play.mvc.Result;
import utils.AbstractTests;
import utils.RunMockHelper;
import validation.InstanceValidationHelper;
import validation.utils.ContextValidation;
import validation.utils.ValidationConstants;
import controllers.Constants;
import fr.cea.ig.MongoDBDAO;


public class RunValidationTest extends AbstractTests {
	
	@Test
	 public void testRunValidationOk() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			 Run run = getRun();
			 ContextValidation ctxVal = new ContextValidation(); 
			 run.validate(ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(0);
		}});
	 }
	
	@Test
	 public void testNoDuplicatesLanesValidationOk() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		     Run run = null;	   
			 run = getExistingRun();
			 ArrayList<Lane> l = new ArrayList<Lane>();
			 Lane lane = getLane();
			 l.add(lane);
			 lane = getLane2();
			 l.add(lane);
			 run.lanes = l;
			 ContextValidation ctxVal = new ContextValidation(); 
			 ctxVal.contextObjects.put("run", run);
			 InstanceValidationHelper.validationLanes(run.lanes, ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(0);
		 }});
	 }
	
	@Test
	 public void testDuplicatesLanesValidationErreur() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			 Run run = getExistingRun();
			 ArrayList<Lane> l = new ArrayList<Lane>();
			 Lane lane = getLane();
			 l.add(lane);
			 lane = getLane();
			 l.add(lane);
			 run.lanes = l;
			 ContextValidation ctxVal = new ContextValidation(); 
			 ctxVal.contextObjects.put("run", run);
			 InstanceValidationHelper.validationLanes(run.lanes, ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(1);
			 assertThat(ctxVal.errors.toString()).contains(ValidationConstants.ERROR_NOTUNIQUE); 
		 }});
	 }

	 
	@Test
	 public void testUpdateLaneValidationOK() {
		 
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		 
			Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET"));
			if(runDelete!=null){
				MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
		
			Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET");
			run.dispatch = true; // For the archive test
			Lane lane = RunMockHelper.newLane(1);
			Lane lane2 = RunMockHelper.newLane(2);
			List<Lane> lanes = new ArrayList<Lane>();
			
			ReadSet readset = RunMockHelper.newReadSet("ReadSetBasicWithRun");
			List<ReadSet> readsets = new ArrayList<ReadSet>();
			readsets.add(readset);
			lane.readsets = readsets;
			lanes.add(lane);
			lanes.add(lane2);
			run.lanes = lanes;
			
			callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
			
			 if (lane.abort != TBoolean.TRUE) {
				 lane.abort = TBoolean.TRUE;
			 }
			 else {
					 lane.abort = TBoolean.FALSE;
			 }
			 lanes.remove(0);
			 lanes.add(lane); 
			 run.lanes = lanes;
			 
			 ContextValidation ctxVal = new ContextValidation(); 
			 ctxVal.contextObjects.put("run", run);
			 InstanceValidationHelper.validationLanes(run.lanes, ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(0);
		 
		 }});
		 
	 }


	@Test
	 public void testUpdateLaneValidationErreur() {
		 
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		 
			Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET"));
			if(runDelete!=null){
				MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
		
			Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET");
			run.dispatch = true; // For the archive test
			Lane lane = RunMockHelper.newLane(1);
			Lane lane2 = RunMockHelper.newLane(2);
			List<Lane> lanes = new ArrayList<Lane>();
			
			ReadSet readset = RunMockHelper.newReadSet("ReadSetBasicWithRun");
			List<ReadSet> readsets = new ArrayList<ReadSet>();
			readsets.add(readset);
			lane.readsets = readsets;
			lanes.add(lane);
			lanes.add(lane2);
			run.lanes = lanes;
			
			callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
			 
			 Lane lane3 = RunMockHelper.newLane(2);
			 lanes.add(lane3); // adding again a lane with number 2
			 run.lanes = lanes;
			 
			 ContextValidation ctxVal = new ContextValidation(); 
			 ctxVal.contextObjects.put("run", run);
			 InstanceValidationHelper.validationLanes(run.lanes, ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(1);
			 assertThat(ctxVal.errors.toString()).contains(ValidationConstants.ERROR_NOTUNIQUE); 
		 
		 }});
		 
	 }	
	
	
	
	@Test
	 public void testLaneValidationOk(){
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		    	   
		     Run run = getRun();
		     ArrayList<Lane> l = new ArrayList<Lane>();
			 Lane lane = getLane();
			 l.add(lane);
			 run.lanes = l;		 
			 ContextValidation ctxVal = new ContextValidation();
			 if (run != null) {
					 ctxVal.rootKeyName ="lanes[0]";
					 ctxVal.contextObjects.put("run", run);
			 }		 
			 run.lanes.get(0).validate(ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(0);
	       }});
	 }
	 
	 
	@Test
	 public void testReadSetValidationOk(){
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		    	   
			Run run = getRun();
			ArrayList<Lane> l = new ArrayList<Lane>();
			 Lane lane = getLane();
			 l.add(lane);
			 run.lanes = l;
			 ReadSet r = getReadSet();
			 ArrayList<ReadSet> ar = new ArrayList<ReadSet>();
			 ar.add(r);
			 run.lanes.get(0).readsets = ar;
	
			 ContextValidation ctxVal = new ContextValidation();
			 ctxVal.contextObjects.put("run",run);
			 ctxVal.contextObjects.put("lane",lane);		 
			 ctxVal.rootKeyName = "lanes[0].readsets[0]";
			 r.validate(ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(0);
		}});
	 }
	 
	 
	 @Test
	 public void testExistingReadSetValidationErreur(){
		 
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		 
				Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET"));
				if(runDelete!=null){
					MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
				}
			
				Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET");
				run.dispatch = true; // For the archive test
				Lane lane = RunMockHelper.newLane(1);
				Lane lane2 = RunMockHelper.newLane(2);
				List<Lane> lanes = new ArrayList<Lane>();
				
				ReadSet readset = RunMockHelper.newReadSet("ReadSetBasicWithRun");
				List<ReadSet> readsets = new ArrayList<ReadSet>();
				readsets.add(readset);
				lane.readsets = readsets;
				lanes.add(lane);
				lanes.add(lane2);
				run.lanes = lanes;
				
				Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		        assertThat(status(result)).isEqualTo(OK);
		
				 // add the same readset as before (ReadSetBasicWithRun) !
				lane2.readsets = readsets;
				lanes.remove(1);
				lanes.add(lane2);
				run.lanes = lanes;		
				
				 ContextValidation ctxVal = new ContextValidation();
				 ctxVal.contextObjects.put("run",run);
				 ctxVal.contextObjects.put("lane",lane);
				 ctxVal.rootKeyName = "lanes[1].readsets[0]";
				 readset.validate(ctxVal);
			
				 Map<String, List<ValidationError>> errorToFind = new HashMap<String, List<ValidationError>>();
				 List<Object> vaArg = new ArrayList<Object>();
				 vaArg.add(readset.code);
				 ValidationError ve = new  ValidationError(ctxVal.rootKeyName + ".code", ValidationConstants.ERROR_NOTUNIQUE, vaArg);
				 ArrayList<ValidationError> al = new ArrayList<ValidationError>();
				 al.add(ve);
				 errorToFind.put(ctxVal.rootKeyName + ".code", al);
				
				 assertThat(ctxVal.errors).hasSize(1);
				 //assertThat(ctxVal.errors.toString()).isEqualTo(errorToFind.toString());
				 assertThat(ctxVal.errors.toString()).contains(ValidationConstants.ERROR_NOTUNIQUE);
			 
		   }});
	 }
	 
	 
	 
	@Test
	 public void testFileValidationOk(){
		
			// au cas où le testFileValidationErreur() vient de passer...
			Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET21"));
			if(runDelete!=null){
				MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
		 
			runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET2"));
			if(runDelete!=null){
				MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
		
			Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET2");
			run.dispatch = true; // For the archive test
			Lane lane = RunMockHelper.newLane(1);
			List<File> files =  new ArrayList<File>();
			File file = RunMockHelper.newFile("xxE410_FA_B00FPMH_3_2_C17GFACXX.IND6.fastq");
			files.add(file);
			List<ReadSet> readsets =  new ArrayList<ReadSet>();
			ReadSet readset = RunMockHelper.newReadSet("test1");
			readset.files = files;
			readsets.add(readset);
			lane.readsets = readsets;
			Lane lane2 = RunMockHelper.newLane(2);
			List<Lane> lanes = new ArrayList<Lane>();
			lanes.add(lane);
			lanes.add(lane2);
			run.lanes = lanes;
			
			 ContextValidation ctxVal = new ContextValidation();
			 ctxVal.contextObjects.put("run",run);
			 ctxVal.rootKeyName = "lanes[0].readsets[0].files[0]";
			 file.validate(ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(0);		 
	 }
	
	
	@Test
	 public void testFileValidationErreur(){
		
		// creation of the file
		
		Run runDelete = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET21"));
		if(runDelete!=null){
			MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
	
		Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET21");
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
		 
		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		result = callAction(controllers.runs.api.routes.ref.Files.save("test1"),fakeRequest().withJsonBody(RunMockHelper.getJsonFile(RunMockHelper.newFile("xxE410_FA_B00FPMH_3_2_C17GFACXX.IND6.fastq"))));
        
		assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/json");
        assertThat(charset(result)).isEqualTo("utf-8");
        
        // test to validate existence of error when duplication of the same file !
        
		 ContextValidation ctxVal = new ContextValidation();
		 ctxVal.contextObjects.put("run",run);
		 File file = getExistingFile();
		 file.validate(ctxVal);
		 
		 Map<String, List<ValidationError>> errorToFind = new HashMap<String, List<ValidationError>>();
		 List<Object> vaArg = new ArrayList<Object>();
		 vaArg.add(file.fullname);
		 ValidationError ve = new  ValidationError("fullname", ValidationConstants.ERROR_NOTUNIQUE, vaArg);
		 ArrayList<ValidationError> al = new ArrayList<ValidationError>();
		 al.add(ve);
		 errorToFind.put("fullname", al);
		 
		 assertThat(ctxVal.errors).hasSize(1);
		 assertThat(ctxVal.errors.toString()).isEqualTo(errorToFind.toString()); 
	 }
	
	 
	
	private Lane getExistingLane(){
		
		Lane lane = new Lane();
		lane.number = 2;
		
		lane.properties.put("nbCycleRead1",new PropertyValue("1056"));
		lane.properties.put("nbCycleReadIndex1",new PropertyValue("1056"));
		lane.properties.put("nbCycleRead2",new PropertyValue("1056"));
		lane.properties.put("nbCycleReadIndex2",new PropertyValue("1056"));
		lane.properties.put("nbCluster",new PropertyValue("1056"));
		lane.properties.put("nbBaseInternalAndIlluminaFilter",new PropertyValue("1056"));
		lane.properties.put("phasing",new PropertyValue("phasing"));
		lane.properties.put("prephasing",new PropertyValue("prephasing"));
		lane.properties.put("nbClusterInternalAndIlluminaFilter",new PropertyValue("1056"));
		lane.properties.put("nbClusterIlluminaFilter",new PropertyValue("1056"));
		lane.properties.put("percentClusterIlluminaFilter",new PropertyValue("99.99"));		
		lane.properties.put("percentClusterInternalAndIlluminaFilter",new PropertyValue("99.99"));
		
		return lane;
	}
	
	
	private Lane getLane(){
		
		Lane lane = new Lane();
		lane.number = 20;
		
		lane.properties.put("nbCycleRead1",new PropertyValue("1056"));
		lane.properties.put("nbCycleReadIndex1",new PropertyValue("1056"));
		lane.properties.put("nbCycleRead2",new PropertyValue("1056"));
		lane.properties.put("nbCycleReadIndex2",new PropertyValue("1056"));
		lane.properties.put("nbCluster",new PropertyValue("1056"));
		lane.properties.put("nbBaseInternalAndIlluminaFilter",new PropertyValue("1056"));
		lane.properties.put("phasing",new PropertyValue("phasing"));
		lane.properties.put("prephasing",new PropertyValue("prephasing"));
		lane.properties.put("nbClusterInternalAndIlluminaFilter",new PropertyValue("1056"));
		lane.properties.put("nbClusterIlluminaFilter",new PropertyValue("1056"));
		lane.properties.put("percentClusterIlluminaFilter",new PropertyValue("99.99"));		
		lane.properties.put("percentClusterInternalAndIlluminaFilter",new PropertyValue("99.99"));
		
		return lane;
	}
	
	
	
	private Lane getLane2(){
		
		Lane lane = new Lane();
		lane.number = 3;
		
		lane.properties.put("nbCycleRead1",new PropertyValue("1056"));
		lane.properties.put("nbCycleReadIndex1",new PropertyValue("1056"));
		lane.properties.put("nbCycleRead2",new PropertyValue("1056"));
		lane.properties.put("nbCycleReadIndex2",new PropertyValue("1056"));
		lane.properties.put("nbCluster",new PropertyValue("1056"));
		lane.properties.put("nbBaseInternalAndIlluminaFilter",new PropertyValue("1056"));
		lane.properties.put("phasing",new PropertyValue("phasing"));
		lane.properties.put("prephasing",new PropertyValue("prephasing"));
		lane.properties.put("nbClusterInternalAndIlluminaFilter",new PropertyValue("1056"));
		lane.properties.put("nbClusterIlluminaFilter",new PropertyValue("1056"));
		lane.properties.put("percentClusterIlluminaFilter",new PropertyValue("99.99"));		
		lane.properties.put("percentClusterInternalAndIlluminaFilter",new PropertyValue("99.99"));
		
		return lane;
	}
	
	
	private ReadSet getReadSet() {
		ReadSet readSet = new ReadSet();
		readSet.code = "CORE_R1";
		readSet.path = "/path/test";
		readSet.projectCode = "PCODE"; 
		readSet.sampleCode = "SPCODE";
		readSet.sampleContainerCode = "SPCONTCODE";
		//readSet.properties.put("insertLength", new PropertyValue("1056"));
		readSet.properties.put("nbUsableBase", new PropertyValue("4565456465"));
		readSet.properties.put("nbUsableCluster", new PropertyValue("132132132132"));
		readSet.properties.put("q30",new PropertyValue("10.23"));
		readSet.properties.put("score", new PropertyValue("0.636"));
		readSet.properties.put("nbRead", new PropertyValue("33"));
		
		readSet.properties.put("nbClusterInternalAndIlluminaFilter", new PropertyValue("4565456465"));
		readSet.properties.put("nbBaseInternalAndIlluminaFilter", new PropertyValue("4565456465"));
		readSet.properties.put("fraction", new PropertyValue("0.2"));

		return readSet;
	}
	
	
	private ReadSet getExistingReadSet() {
		ReadSet readSet = new ReadSet();
		readSet.code = "ReadSetBasicWithRun3";
		readSet.path = "/path/test";
		readSet.projectCode = "PCODE"; 
		readSet.sampleCode = "SPCODE";
		readSet.sampleContainerCode = "SPCONTCODE";
		//readSet.properties.put("insertLength", new PropertyValue("1056"));
		readSet.properties.put("nbUsableBase", new PropertyValue("4565456465"));
		readSet.properties.put("nbUsableCluster", new PropertyValue("132132132132"));
		readSet.properties.put("q30",new PropertyValue("10.23"));
		readSet.properties.put("score", new PropertyValue("0.636"));
		readSet.properties.put("nbRead", new PropertyValue("33"));
		
		readSet.properties.put("nbClusterInternalAndIlluminaFilter", new PropertyValue("4565456465"));
		readSet.properties.put("nbBaseInternalAndIlluminaFilter", new PropertyValue("4565456465"));
		readSet.properties.put("fraction", new PropertyValue("0.2"));

		return readSet;
	}
	
	 private File getFile() {
		File file = new File();
		file.extension = "fst";
		file.fullname = "fullname.fst";
		file.typeCode = "RAW";
		file.usable = Boolean.FALSE;
		
		file.properties.put("asciiEncoding", new PropertyValue("33"));
		file.properties.put("label", new PropertyValue("READ1"));		
		return file;
	}
	 
	 private File getExistingFile() {
		File file = new File();
		file.extension = "fastq";
		file.fullname = "xxE410_FA_B00FPMH_3_2_C17GFACXX.IND6.fastq";
		file.typeCode = "RAW";
		file.usable = Boolean.FALSE;
		
		file.properties.put("asciiEncoding", new PropertyValue("33"));
		file.properties.put("label", new PropertyValue("READ1"));		
		return file;
	}
	 
	 
	private Run getRun() {
			Run run = new Run();
			run.code = "YANN_TEST1FORREADSET0";
			run.typeCode = "RHS2000";
			run.containerSupportCode = "FC00000";
			//run.dispatch = True;
			
			run.traceInformation = new TraceInformation();
			run.traceInformation.setTraceInformation("test");
			
			run.properties.put("nbCycle", new PropertyValue("25"));		
			run.properties.put("nbClusterTotal", new PropertyValue("25"));
			run.properties.put("nbClusterIlluminaFilter", new PropertyValue("25"));
			run.properties.put("nbBase", new PropertyValue("25"));
			run.properties.put("flowcellPosition", new PropertyValue("25"));
			run.properties.put("rtaVersion", new PropertyValue("25"));
			run.properties.put("flowcellVersion", new PropertyValue("25"));
			run.properties.put("controlLane", new PropertyValue("25"));
			run.properties.put("mismatch", new PropertyValue("False"));
			
			run.instrumentUsed = new InstrumentUsed();
			run.instrumentUsed.code = "HS7";
			run.instrumentUsed.categoryCode = "HISEQ2000";
			
			return run;
		}
	
	private Run getExistingRun() {
		Run run = new Run();
		run.code = "YANN_TEST1FORREADSET";
		run.typeCode = "RHS2000";
		run.containerSupportCode = "FC00000";
		//run.dispatch = True;
		
		run.traceInformation = new TraceInformation();
		run.traceInformation.setTraceInformation("test");
		
		run.properties.put("nbCycle", new PropertyValue("25"));		
		run.properties.put("nbClusterTotal", new PropertyValue("25"));
		run.properties.put("nbClusterIlluminaFilter", new PropertyValue("25"));
		run.properties.put("nbBase", new PropertyValue("25"));
		run.properties.put("flowcellPosition", new PropertyValue("25"));
		run.properties.put("rtaVersion", new PropertyValue("25"));
		run.properties.put("flowcellVersion", new PropertyValue("25"));
		run.properties.put("controlLane", new PropertyValue("25"));
		run.properties.put("mismatch", new PropertyValue("False"));
		
		run.instrumentUsed = new InstrumentUsed();
		run.instrumentUsed.code = "HS7";
		run.instrumentUsed.categoryCode = "HISEQ2000";
		
		
		return run;
	}

}
