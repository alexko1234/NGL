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

import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.InstrumentUsed;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import net.vz.mongodb.jackson.DBQuery;

import org.junit.Test;

import play.data.validation.ValidationError;
import play.mvc.Result;
import utils.AbstractTests;
import utils.RunMockHelper;
import validation.ContextValidation;
import validation.InstanceValidationHelper;
import validation.utils.ValidationConstants;
import controllers.Constants;
import fr.cea.ig.MongoDBDAO;


public class RunValidationTest extends AbstractTests {
	
	@Test
	 public void testEntireRunValidationTraceAllErrors() {
		// just to show all errors messages (key, value) and show in that way the manner it looks like !;
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			 Run run = getEmptyRun();
			 
			 ArrayList<Lane> al = new ArrayList<Lane>();
			 Lane l = getEmptyLane();
			 
			 
			 ReadSet r = getEmptyReadSet();
			 ArrayList<ReadSet> ar = new ArrayList<ReadSet>();
			
			 File f = getEmptyFile();
			 ArrayList<File> af = new ArrayList<File>();
			 af.add(f);
			 r.files = af;
			 
			 ar.add(r);
			 l.readsets = ar;
			 
			 al.add(l);
			 run.lanes = al;
			 
			 HashMap<String, List<ValidationError>> e = new HashMap<String, List<ValidationError>>();
			 ContextValidation ctxVal = new ContextValidation(e);   
			 ctxVal.setRootKeyName("");
			 
			 run.validate(ctxVal); 

			 System.out.println("------------------------------------------------------");
					 
			 for (Map.Entry<String, List<ValidationError>> entry : ctxVal.errors.entrySet()) {
			     System.out.println(entry.getKey() + "---------------------------------->" + entry.getValue().toString());
			 }
			 
			 System.out.println("-------------------------------------------------------");

		}});
	 }
	
	
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
			 ctxVal.putObject("run", run);
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
			 ctxVal.putObject("run", run);
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
			 ctxVal.putObject("run", run);
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
			 ctxVal.putObject("run", run);
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
					 ctxVal.setRootKeyName("lanes[0]");
					 ctxVal.putObject("run", run);
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
			 ctxVal.putObject("run",run);
			 ctxVal.putObject("lane",lane);		 
			 ctxVal.setRootKeyName("lanes[0].readsets[0]");
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
				 ctxVal.putObject("run",run);
				 ctxVal.putObject("lane",lane);
				 ctxVal.setRootKeyName("lanes[1].readsets[0]");
				 readset.validate(ctxVal);
			
				 Map<String, List<ValidationError>> errorToFind = new HashMap<String, List<ValidationError>>();
				 List<Object> vaArg = new ArrayList<Object>();
				 vaArg.add(readset.code);
				 ctxVal.addKeyToRootKeyName("code"); 
				 ValidationError ve = new  ValidationError(ctxVal.getRootKeyName(), ValidationConstants.ERROR_NOTUNIQUE, vaArg);
				 ArrayList<ValidationError> al = new ArrayList<ValidationError>();
				 al.add(ve);
				 errorToFind.put(ctxVal.getRootKeyName(), al);
				
				 assertThat(ctxVal.errors).hasSize(1);
				 //assertThat(ctxVal.errors.toString()).isEqualTo(errorToFind.toString());
				 assertThat(ctxVal.errors.toString()).contains(ValidationConstants.ERROR_NOTUNIQUE);
			 
		   }});
	 }
	 
	 
	@Test
	 public void testFileValidationOk(){
		
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
			 ctxVal.putObject("run",run);
			 ctxVal.setRootKeyName("lanes[0].readsets[0].files[0]");
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
		 ctxVal.putObject("run",run);
		 File file = getExistingFile();
		 ctxVal.setRootKeyName("");
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
	
	
	private Lane getLane(){
		
		Lane lane = new Lane();
		lane.number = 20;
		
		lane.properties.put("nbCycleRead1",new PropertySingleValue("1056"));
		lane.properties.put("nbCycleReadIndex1",new PropertySingleValue("1056"));
		lane.properties.put("nbCycleRead2",new PropertySingleValue("1056"));
		lane.properties.put("nbCycleReadIndex2",new PropertySingleValue("1056"));
		lane.properties.put("nbCluster",new PropertySingleValue("1056"));
		lane.properties.put("nbBaseInternalAndIlluminaFilter",new PropertySingleValue("1056"));
		lane.properties.put("phasing",new PropertySingleValue("phasing"));
		lane.properties.put("prephasing",new PropertySingleValue("prephasing"));
		lane.properties.put("nbClusterInternalAndIlluminaFilter",new PropertySingleValue("1056"));
		lane.properties.put("nbClusterIlluminaFilter",new PropertySingleValue("1056"));
		lane.properties.put("percentClusterIlluminaFilter",new PropertySingleValue("99.99"));		
		lane.properties.put("percentClusterInternalAndIlluminaFilter",new PropertySingleValue("99.99"));
		
		return lane;
	}
	
	
	private Lane getEmptyLane(){
		
		Lane lane = new Lane();
		lane.number = 0;
		
		lane.properties.put("nbCycleRead1",new PropertySingleValue(""));
		lane.properties.put("nbCycleReadIndex1",new PropertySingleValue(""));
		lane.properties.put("nbCycleRead2",new PropertySingleValue(""));
		lane.properties.put("nbCycleReadIndex2",new PropertySingleValue(""));
		lane.properties.put("nbCluster",new PropertySingleValue(""));
		lane.properties.put("nbBaseInternalAndIlluminaFilter",new PropertySingleValue(""));
		lane.properties.put("phasing",new PropertySingleValue(""));
		lane.properties.put("prephasing",new PropertySingleValue(""));
		lane.properties.put("nbClusterInternalAndIlluminaFilter",new PropertySingleValue(""));
		lane.properties.put("nbClusterIlluminaFilter",new PropertySingleValue(""));
		lane.properties.put("percentClusterIlluminaFilter",new PropertySingleValue(""));		
		lane.properties.put("percentClusterInternalAndIlluminaFilter",new PropertySingleValue(""));
		
		return lane;
	}
	
	
	
	private Lane getLane2(){
		
		Lane lane = new Lane();
		lane.number = 3;
		
		lane.properties.put("nbCycleRead1",new PropertySingleValue("1056"));
		lane.properties.put("nbCycleReadIndex1",new PropertySingleValue("1056"));
		lane.properties.put("nbCycleRead2",new PropertySingleValue("1056"));
		lane.properties.put("nbCycleReadIndex2",new PropertySingleValue("1056"));
		lane.properties.put("nbCluster",new PropertySingleValue("1056"));
		lane.properties.put("nbBaseInternalAndIlluminaFilter",new PropertySingleValue("1056"));
		lane.properties.put("phasing",new PropertySingleValue("phasing"));
		lane.properties.put("prephasing",new PropertySingleValue("prephasing"));
		lane.properties.put("nbClusterInternalAndIlluminaFilter",new PropertySingleValue("1056"));
		lane.properties.put("nbClusterIlluminaFilter",new PropertySingleValue("1056"));
		lane.properties.put("percentClusterIlluminaFilter",new PropertySingleValue("99.99"));		
		lane.properties.put("percentClusterInternalAndIlluminaFilter",new PropertySingleValue("99.99"));
		
		return lane;
	}
	
	
	private ReadSet getReadSet() {
		ReadSet readSet = new ReadSet();
		readSet.code = "CORE_R1";
		readSet.path = "/path/test";
		readSet.projectCode = "PCODE"; 
		readSet.sampleCode = "SPCODE";
		readSet.sampleContainerCode = "SPCONTCODE";
		//readSet.properties.put("insertLength", new PropertySingleValue("1056"));
		readSet.properties.put("nbUsableBase", new PropertySingleValue("4565456465"));
		readSet.properties.put("nbUsableCluster", new PropertySingleValue("132132132132"));
		readSet.properties.put("q30",new PropertySingleValue("10.23"));
		readSet.properties.put("score", new PropertySingleValue("0.636"));
		readSet.properties.put("nbRead", new PropertySingleValue("33"));
		
		readSet.properties.put("nbClusterInternalAndIlluminaFilter", new PropertySingleValue("4565456465"));
		readSet.properties.put("nbBaseInternalAndIlluminaFilter", new PropertySingleValue("4565456465"));
		readSet.properties.put("fraction", new PropertySingleValue("0.2"));

		return readSet;
	}
	
	
	private ReadSet getEmptyReadSet() {
		ReadSet readSet = new ReadSet();
		readSet.code = "";
		readSet.path = "";
		readSet.projectCode = ""; 
		readSet.sampleCode = "";
		readSet.sampleContainerCode = "";
		readSet.properties.put("nbUsableBase", new PropertySingleValue(""));
		readSet.properties.put("nbUsableCluster", new PropertySingleValue(""));
		readSet.properties.put("q30",new PropertySingleValue(""));
		readSet.properties.put("score", new PropertySingleValue(""));
		readSet.properties.put("nbRead", new PropertySingleValue(""));
		readSet.properties.put("nbClusterInternalAndIlluminaFilter", new PropertySingleValue(""));
		readSet.properties.put("nbBaseInternalAndIlluminaFilter", new PropertySingleValue(""));
		readSet.properties.put("fraction", new PropertySingleValue(""));

		return readSet;
	}
	 
	 private File getEmptyFile() {
		File file = new File();
		file.extension = "";
		file.fullname = "";
		file.typeCode = "";
		file.usable = null;
		
		file.properties.put("asciiEncoding", new PropertySingleValue(""));
		file.properties.put("label", new PropertySingleValue(""));		
		return file;
	}
	 
	 private File getExistingFile() {
		File file = new File();
		file.extension = "fastq";
		file.fullname = "xxE410_FA_B00FPMH_3_2_C17GFACXX.IND6.fastq";
		file.typeCode = "RAW";
		file.usable = Boolean.FALSE;
		
		file.properties.put("asciiEncoding", new PropertySingleValue("33"));
		file.properties.put("label", new PropertySingleValue("READ1"));		
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
			
			run.properties.put("nbCycle", new PropertySingleValue("25"));		
			run.properties.put("nbClusterTotal", new PropertySingleValue("25"));
			run.properties.put("nbClusterIlluminaFilter", new PropertySingleValue("25"));
			run.properties.put("nbBase", new PropertySingleValue("25"));
			run.properties.put("flowcellPosition", new PropertySingleValue("25"));
			run.properties.put("rtaVersion", new PropertySingleValue("25"));
			run.properties.put("flowcellVersion", new PropertySingleValue("25"));
			run.properties.put("controlLane", new PropertySingleValue("25"));
			run.properties.put("mismatch", new PropertySingleValue("False"));
			
			run.instrumentUsed = new InstrumentUsed();
			run.instrumentUsed.code = "HS7";
			run.instrumentUsed.categoryCode = "HISEQ2000";
			
			return run;
		}
	
	
	private Run getEmptyRun() {
		Run run = new Run();
		run.code = "";
		run.typeCode = "";
		run.containerSupportCode = "";
		
		run.traceInformation = new TraceInformation();
		run.traceInformation.setTraceInformation("test");
	
		//run.properties.put("nbCycle", new PropertyValue(""));		
		run.properties.put("nbClusterTotal", new PropertySingleValue(""));
		run.properties.put("nbClusterIlluminaFilter", new PropertySingleValue(""));
		run.properties.put("nbBase", new PropertySingleValue(""));
		run.properties.put("flowcellPosition", new PropertySingleValue(""));
		run.properties.put("rtaVersion", new PropertySingleValue(""));
		run.properties.put("flowcellVersion", new PropertySingleValue(""));
		run.properties.put("controlLane", new PropertySingleValue(""));
		run.properties.put("mismatch", new PropertySingleValue(""));
		
		run.instrumentUsed = new InstrumentUsed();
		run.instrumentUsed.code = "";
		run.instrumentUsed.categoryCode = "";
	
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
		
		run.properties.put("nbCycle", new PropertySingleValue("25"));		
		run.properties.put("nbClusterTotal", new PropertySingleValue("25"));
		run.properties.put("nbClusterIlluminaFilter", new PropertySingleValue("25"));
		run.properties.put("nbBase", new PropertySingleValue("25"));
		run.properties.put("flowcellPosition", new PropertySingleValue("25"));
		run.properties.put("rtaVersion", new PropertySingleValue("25"));
		run.properties.put("flowcellVersion", new PropertySingleValue("25"));
		run.properties.put("controlLane", new PropertySingleValue("25"));
		run.properties.put("mismatch", new PropertySingleValue("False"));
		
		run.instrumentUsed = new InstrumentUsed();
		run.instrumentUsed.code = "HS7";
		run.instrumentUsed.categoryCode = "HISEQ2000";
		
		return run;
	}

}
