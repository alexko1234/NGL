package models.instances.validation;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Validation;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.InstrumentUsed;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;

import org.junit.Test;

import play.data.validation.ValidationError;
import play.mvc.Result;
import utils.AbstractTests;
import utils.RunMockHelper;
import validation.ContextValidation;
import validation.run.instance.LaneValidationHelper;
import validation.utils.ValidationConstants;
import fr.cea.ig.MongoDBDAO;
import static utils.RunMockHelper.*;

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
			
			 File f = getEmptyFile();
			 ArrayList<File> af = new ArrayList<File>();
			 af.add(f);
			 r.files = af;
			 
			 al.add(l);
			 run.lanes = al;
			 
			 ContextValidation ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 run.validate(ctxVal); 
			 
			 System.out.println("------------------------------------------------------");
					 
			 for (Map.Entry<String, List<ValidationError>> entry : ctxVal.errors.entrySet()) {
			     System.out.println(entry.getKey() + "---------------------------------->" + entry.getValue().toString());
			 }
			 
			 System.out.println("-------------------------------------------------------");
			 
			 ctxVal = new ContextValidation(); 
			 ctxVal.setCreationMode();
			 r.validate(ctxVal); 
			 
			 for (Map.Entry<String, List<ValidationError>> entry : ctxVal.errors.entrySet()) {
			     System.out.println(entry.getKey() + "---------------------------------->" + entry.getValue().toString());
			 }

		}});
	 }
	
	
	 @Test
	 public void testCreateRunValidationOK() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}	   
			 Run run = getFullRun();
			 ContextValidation ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 run.validate(ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(0);
		}});
	 }
	
	
	 @Test
	 public void testUpdateRunValidationOK() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}	   
			 Run run = getFullRun();
			 ContextValidation ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 run.validate(ctxVal);
			
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
			assertThat(status(result)).isEqualTo(OK);
			
	        run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
	        
			run.containerSupportCode = "ZOUIOUI";
			run.traceInformation.modifyDate = new Date();
			run.traceInformation.modifyUser = "dnoisett";
			
			ctxVal = new ContextValidation();
			 ctxVal.setUpdateMode();
			 run.validate(ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(0);
		}});
	 }

	 @Test
	 public void testValidationErrorBadRunType() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}	   
			 Run run = getFullRun();
			 run.typeCode = "2500";
			 ContextValidation ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 run.validate(ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(2);
			 assertThat(ctxVal.errors.toString()).contains("typeCode");
			 
		}});
	 }
	 
	 
	 @Test
	 public void testRunWith1TreatmentValidationOK() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}	   
			 Run run = getFullRun();
			 
			 Treatment t1 = new Treatment();
			 t1.code = "ngsrg";
			 t1.typeCode = "ngsrg-illumina";
			 t1.categoryCode = "ngsrg";
			//define map of single property values
			Map<String,PropertyValue> m = new HashMap<String,PropertyValue>();
			m.put("nbClusterTotal", new PropertySingleValue(100)); // valeur simple
			m.put("nbClusterIlluminaFilter", new PropertySingleValue(100));
			m.put("nbCycle", new PropertySingleValue(100));
			m.put("nbBase", new PropertySingleValue(100));
			m.put("flowcellPosition", new PropertySingleValue("A"));
			m.put("rtaVersion", new PropertySingleValue("v1"));
			m.put("flowcellVersion", new PropertySingleValue("v1"));
			m.put("controlLane", new PropertySingleValue(90));
			m.put("mismatch", new PropertySingleValue(true));
			t1.set("default", m);
			 
			 Map<String, Treatment> mT = new HashMap<String, Treatment>();
			 mT.put("ngsrg", t1);
			 run.treatments = mT;
			
			 ContextValidation ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 run.validate(ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(0);
			 
		}});
	 }
	 
	 
	 @Test
	 public void testError1TreatmentWithoutResult() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}	   
			 Run run = getFullRun();
			 
			 Treatment t1 = new Treatment();
			 t1.code = "ngsrg";
			 t1.typeCode = "ngsrg-illumina";
			 t1.categoryCode = "ngsrg";
			//define map of single property values
			Map<String,PropertyValue> m = new HashMap<String,PropertyValue>();
			m.put("nbClusterTotal", new PropertySingleValue(100)); // valeur simple
			m.put("nbClusterIlluminaFilter", new PropertySingleValue(100));
			m.put("nbCycle", new PropertySingleValue(100));
			m.put("nbBase", new PropertySingleValue(100));
			m.put("flowcellPosition", new PropertySingleValue("A"));
			m.put("rtaVersion", new PropertySingleValue("v1"));
			m.put("flowcellVersion", new PropertySingleValue("v1"));
			m.put("controlLane", new PropertySingleValue(90));
			m.put("mismatch", new PropertySingleValue(true));
			t1.set("default", m);
			
			 Treatment t2 = new Treatment();
			 t2.code = "sav";
			 t2.typeCode = "sav";
			 t2.categoryCode = "sequencing";
			 
			 Map<String, Treatment> mT = new HashMap<String, Treatment>();
			 mT.put("ngsrg", t1);
			 mT.put("sav", t2);
			 run.treatments = mT;
			
			 ContextValidation ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 run.validate(ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(1);
			assertThat(ctxVal.errors.toString()).contains("treatments.sav.result");
			 
		}});
	 }
	 
	 
	 @Test
	 public void testError2IdenticalTreatments() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}	   
			 Run run = getFullRun();
			 
			 Treatment t1 = new Treatment();
			 t1.code = "ngsrg";
			 t1.typeCode = "ngsrg-illumina";
			 t1.categoryCode = "ngsrg";
			//define map of single property values
			Map<String,PropertyValue> m = new HashMap<String,PropertyValue>();
			m.put("nbClusterTotal", new PropertySingleValue(100)); // valeur simple
			m.put("nbClusterIlluminaFilter", new PropertySingleValue(100));
			m.put("nbCycle", new PropertySingleValue(100));
			m.put("nbBase", new PropertySingleValue(100));
			m.put("flowcellPosition", new PropertySingleValue("A"));
			m.put("rtaVersion", new PropertySingleValue("v1"));
			m.put("flowcellVersion", new PropertySingleValue("v1"));
			m.put("controlLane", new PropertySingleValue(90));
			m.put("mismatch", new PropertySingleValue(true));
			t1.set("default", m);
			
			 Treatment t2 = new Treatment();
			 t2.code = "ngsrg";
			 t2.typeCode = "ngsrg-illumina";
			 t2.categoryCode = "ngsrg";
			//define map of single property values
			m = new HashMap<String,PropertyValue>();
			m.put("nbClusterTotal", new PropertySingleValue(100)); // valeur simple
			m.put("nbClusterIlluminaFilter", new PropertySingleValue(100));
			m.put("nbCycle", new PropertySingleValue(100));
			m.put("nbBase", new PropertySingleValue(100));
			m.put("flowcellPosition", new PropertySingleValue("A"));
			m.put("rtaVersion", new PropertySingleValue("v1"));
			m.put("flowcellVersion", new PropertySingleValue("v1"));
			m.put("controlLane", new PropertySingleValue(90));
			m.put("mismatch", new PropertySingleValue(true));
			t2.set("default", m);
			
			 Map<String, Treatment> mT = new HashMap<String, Treatment>();
			 mT.put("ngsrg", t1);
			 mT.put("ngsrg2", t2);
			 run.treatments = mT;
			 
			 System.out.println(""); 
			 System.out.println(""+mT.size()); 
			 System.out.println(""); 
			
			 ContextValidation ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 run.validate(ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(1);
			 assertThat(ctxVal.errors.toString()).contains("treatments.ngsrg.code");
			 
		}});
	 }
	 
	 
	 
	 @Test
	 public void testErrorUpdateBadRunType() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}	   
			 Run run = getFullRun();
			 ContextValidation ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 run.validate(ctxVal);
			
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
			assertThat(status(result)).isEqualTo(OK);
			
	        run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
	        
			run.containerSupportCode = "ZOUIOUI";
			run.traceInformation.modifyDate = new Date();
			run.traceInformation.modifyUser = "dnoisett";
			run.typeCode = "RHS3000";
			
			ctxVal = new ContextValidation();
			 ctxVal.setUpdateMode();
			 run.validate(ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(2);
			 assertThat(ctxVal.errors.toString()).contains("typeCode");
			 
		}});
	 }
	 
	
	
	
	 @Test
	 public void testErrorFieldsRequired() {
		//control the required field exist
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}	   
			 Run run = getEmptyRun();
			 ContextValidation ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 run.validate(ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(8);
			 assertThat(ctxVal.errors.toString()).contains("code"); //1
			 assertThat(ctxVal.errors.toString()).contains("state.code"); //1
			 assertThat(ctxVal.errors.toString()).contains("traceInformation"); //2
			 assertThat(ctxVal.errors.toString()).contains("typeCode"); //1
			 assertThat(ctxVal.errors.toString()).contains("containerSupportCode"); //1
			 assertThat(ctxVal.errors.toString()).contains("instrumentUsed"); //2
		}});
	 }
	
	
	 @Test
	 public void testCreateLanesValidationOK() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {	
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}	   
			 Run run = getFullRun();	   
			 ArrayList<Lane> l = new ArrayList<Lane>();
			 Lane lane = getLane();
			 l.add(lane);
			 lane = getLane2();
			 l.add(lane);
			 run.lanes = l;
			 ContextValidation ctxVal = new ContextValidation(); 
			 ctxVal.putObject("run", run);
			 ctxVal.setCreationMode();
			 LaneValidationHelper.validationLanes(run.lanes, ctxVal);
			 assertThat(ctxVal.errors).hasSize(0);
		 }});
	 }
	
	 @Test
	 public void testCreateLaneValidationOK(){
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}	   
		     Run run = getFullRun();
		     ArrayList<Lane> l = new ArrayList<Lane>();
			 Lane lane = getLane();
			 l.add(lane);
			 run.lanes = l;		 
			 ContextValidation ctxVal = new ContextValidation();
			 ctxVal.putObject("run", run);	 
			 ctxVal.setCreationMode();
			 run.lanes.get(0).validate(ctxVal);
			 assertThat(ctxVal.errors).hasSize(0);
	       }});
	 }
	 
	 @Test
	 public void testErrorLanesValidation() {
		// run with 2 identical lanes
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}	   
			 Run run = getFullRun();
			 ArrayList<Lane> l = new ArrayList<Lane>();
			 Lane lane = getLane();
			 l.add(lane);
			 lane = getLane();
			 l.add(lane); // adding again the same lane ! 
			 run.lanes = l;
			 ContextValidation ctxVal = new ContextValidation(); 
			 ctxVal.putObject("run", run);
			 ctxVal.setCreationMode();
			 LaneValidationHelper.validationLanes(run.lanes, ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(1);
			 assertThat(ctxVal.errors.toString()).contains(ValidationConstants.ERROR_NOTUNIQUE_MSG); 
		 }});
	 }
	 
	 @Test
	 public void testUpdateLaneValidationOK() { 
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
			ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code","ReadSetBasicWithRun"));
			if(readSetDelete!=null){
				MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
			}
			
			Run run = getFullRun();
			run.dispatch = true; // For the archive test
			
			Lane lane = RunMockHelper.newLane(1);
			Lane lane2 = RunMockHelper.newLane(2);
			List<Lane> lanes = new ArrayList<Lane>();
			
			ReadSet readSet = RunMockHelper.newReadSet("ReadSetBasicWithRun");
			readSet.runCode = run.code;
			readSet.laneNumber = 1;
			readSet.dispatch = false;
			
			
			List<String> r = new ArrayList<String>();
			lane.readSetCodes = r;
			lane.validation = RunMockHelper.getValidation(TBoolean.TRUE);
			lanes.add(lane);
			lanes.add(lane2);
			run.lanes = lanes;
			
			ContextValidation ctxVal = new ContextValidation(); 
			 ctxVal.putObject("run", run);
			 ctxVal.setCreationMode();
			 LaneValidationHelper.validationLanes(run.lanes, ctxVal);
			 assertThat(ctxVal.errors).hasSize(0);
			
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
			assertThat(status(result)).isEqualTo(OK);
			
			 if (lane.validation.valid.equals(TBoolean.TRUE)) {
				 lane.validation.valid = TBoolean.TRUE;
			 } else {
				lane.validation.valid = TBoolean.FALSE;
			 }
			 	 
			 result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readSet)));
			 assertThat(status(result)).isEqualTo(OK);
			 lane.state = getState("F-RG");
			 ctxVal = new ContextValidation(); 
			 ctxVal.putObject("run", run);
			  ctxVal.setUpdateMode();
			  LaneValidationHelper.validationLanes(run.lanes, ctxVal);
			 assertThat(ctxVal.errors).hasSize(0);
			 
		 }

			});
		 
	 }
	
	
	 @Test
	 public void testUpdateLaneValidationErrorBadState() { 
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
			ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code","ReadSetBasicWithRun"));
			if(readSetDelete!=null){
				MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
			}
			
			Run run = getFullRun();
			run.dispatch = true; // For the archive test
			
			Lane lane = RunMockHelper.newLane(1);
			Lane lane2 = RunMockHelper.newLane(2);
			List<Lane> lanes = new ArrayList<Lane>();
			
			ReadSet readSet = RunMockHelper.newReadSet("ReadSetBasicWithRun");
			readSet.runCode = run.code;
			readSet.laneNumber = 1;
			readSet.dispatch = false;
			
			
			List<String> r = new ArrayList<String>();
			lane.readSetCodes = r;

			lanes.add(lane);
			lanes.add(lane2);
			run.lanes = lanes;
			
			ContextValidation ctxVal = new ContextValidation(); 
			 ctxVal.putObject("run", run);
			 ctxVal.setCreationMode();
			 LaneValidationHelper.validationLanes(run.lanes, ctxVal);
			 assertThat(ctxVal.errors).hasSize(0);
			
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
			assertThat(status(result)).isEqualTo(OK);
			
			if (lane.validation.valid.equals(TBoolean.TRUE)) {
				 lane.validation.valid = TBoolean.TRUE;
			 } else {
				lane.validation.valid = TBoolean.FALSE;
			 }
			 
			 
			 result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readSet)));
			 assertThat(status(result)).isEqualTo(OK);
			 
			 lane.state = getState("A");
			 ctxVal = new ContextValidation(); 
			 ctxVal.putObject("run", run);
			  ctxVal.setUpdateMode();
			  LaneValidationHelper.validationLanes(run.lanes, ctxVal);
			 assertThat(ctxVal.errors).hasSize(1);
			 assertThat(ctxVal.errors.toString()).contains("state.code");
			 assertThat(ctxVal.errors.toString()).contains("valuenotauthorized");
			 		 
		 }

			});
		 
	 }
	
	 

	 @Test
	 public void testUpdateLaneValidationErrorBadLaneNumber() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		 
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
		
			Run run = getFullRun();
			run.dispatch = true; // For the archive test
			Lane lane = RunMockHelper.newLane(1);
			Lane lane2 = RunMockHelper.newLane(2);
			List<Lane> lanes = new ArrayList<Lane>();
			
			lanes.add(lane);
			lanes.add(lane2);
			run.lanes = lanes;
			
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
			assertThat(status(result)).isEqualTo(OK);
			 
			 Lane lane3 = RunMockHelper.newLane(2);
			 lanes.add(lane3); // adding again a lane with number 2
			 run.lanes = lanes;
			 
			 ContextValidation ctxVal = new ContextValidation(); 
			 ctxVal.putObject("run", run);
			 ctxVal.setCreationMode();
			 lane3.validate(ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(1);
			 assertThat(ctxVal.errors.toString()).contains(ValidationConstants.ERROR_NOTUNIQUE_MSG); 
			 
			 lane3.number = 3;
			 ctxVal = new ContextValidation(); 
			 ctxVal.putObject("run", run);
			 ctxVal.setUpdateMode();
			 lane3.validate(ctxVal);
			 
			 assertThat(ctxVal.errors).hasSize(1);
			 assertThat(ctxVal.errors.toString()).contains("number"); 
		 }});
		 
	 }	
	 
	 
	 @Test
	 public void testReadSetValidationOK(){
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		    	   
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
			ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code",getNewReadSet().code));
			if(readSetDelete!=null){
				MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
			}
		    	   
			Run run = getFullRun();
			ArrayList<Lane> l = new ArrayList<Lane>();
			 Lane lane = getLane();
			 
			 ReadSet r = getNewReadSet();
			 r.runCode = run.code;
			 ArrayList<String> a = new ArrayList<String>();
			 //a.add(r.code);			 
			 lane.readSetCodes = a; // empty list
			 
			 l.add(lane);
			 run.lanes = l;

			 ContextValidation ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 run.validate(ctxVal);
			 assertThat(ctxVal.errors).hasSize(0);
			 
			 ctxVal.putObject("run", run);
			 ctxVal.setCreationMode();
			 run.lanes.get(0).validate(ctxVal);
			 assertThat(ctxVal.errors).hasSize(0);
			 
			 // the run must be saved to validate one of his readset 
			 
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
			assertThat(status(result)).isEqualTo(OK);
			
			 ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 r.validate(ctxVal);
			 assertThat(ctxVal.errors).hasSize(0);
				
			result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(r)));
			assertThat(status(result)).isEqualTo(OK);
				
			 a.add(r.code);
			 run.lanes.get(0).readSetCodes = a;
			 
			 ctxVal = new ContextValidation();
			 ctxVal.putObject("run", run);
			 ctxVal.setUpdateMode();
			 run.lanes.get(0).validate(ctxVal);
			 assertThat(ctxVal.errors).hasSize(0);
			 
		}});
	 }
	
	
	 @Test
	 public void testReadSetCodeValidationError(){
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		    	   
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
			ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code",getNewReadSet().code));
			if(readSetDelete!=null){
				MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
			}
		    	   
			Run run = getFullRun();
			ArrayList<Lane> l = new ArrayList<Lane>();
			 Lane lane = getLane();
			 
			 ReadSet r = getNewReadSet();
			 ArrayList<String> a = new ArrayList<String>();
			 //a.add(r.code);			 
			 lane.readSetCodes = a;
			 r.runCode = run.code;
			 
			 l.add(lane);
			 run.lanes = l;

			 ContextValidation ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 run.validate(ctxVal);
			 assertThat(ctxVal.errors).hasSize(0);
			 
			 ctxVal.putObject("run", run);
			 ctxVal.setCreationMode();
			 run.lanes.get(0).validate(ctxVal);
			 r.runCode = run.code;
			 assertThat(ctxVal.errors).hasSize(0);
			 
			 // the run must be saved to validate one of his readset 
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
			assertThat(status(result)).isEqualTo(OK);
				 
			 ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 r.validate(ctxVal);
			 assertThat(ctxVal.errors).hasSize(0);
			 
			result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(r)));
			assertThat(status(result)).isEqualTo(OK);		
			  
			 a.add(r.code);
			 a.add("toto");
			 run.lanes.get(0).readSetCodes = a;
			 
			 ctxVal = new ContextValidation();
			 ctxVal.putObject("run", run);
			 ctxVal.setUpdateMode();
			 run.lanes.get(0).validate(ctxVal);
			 assertThat(ctxVal.errors).hasSize(1); 
			 assertThat(ctxVal.errors.toString()).contains("toto");
		}});
	 }
	 

	 @Test
	 public void testErrorReadSetWithLaneNumber20(){
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		    	   
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
			ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code",getNewReadSet().code));
			if(readSetDelete!=null){
				MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
			}
		    	   
			Run run = getFullRun();
			ArrayList<Lane> l = new ArrayList<Lane>();
			 Lane lane = getLane(); //lane.number = 1
			 // correct value
			 lane.state = getState("F"); 
			 ReadSet r = getNewReadSet();
			 ArrayList<String> a = new ArrayList<String>();
			 //a.add(r.code);			 
			 lane.readSetCodes = a;
			 r.runCode = run.code;
			 
			 //MUST GENERATE A ERROR ! 
			 r.laneNumber = 20;
			 
			 l.add(lane);
			 run.lanes = l;

			 ContextValidation ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 run.validate(ctxVal);
			 assertThat(ctxVal.errors).hasSize(0);	 
			 
			 ctxVal.putObject("run", run);
			 ctxVal.setCreationMode();
			 run.lanes.get(0).validate(ctxVal);
			 r.runCode = run.code;
			 assertThat(ctxVal.errors).hasSize(0);
			 
			 // the run must be saved to validate one of his readset 
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
			assertThat(status(result)).isEqualTo(OK);
			 
			 ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 r.validate(ctxVal);
			 assertThat(ctxVal.errors).hasSize(2);
			 assertThat(ctxVal.errors.toString()).contains("laneNumber");
			 assertThat(ctxVal.errors.toString()).contains("runCode");

		}});
	 }
	 
	 @Test
	 public void testErrorDuplicateReadSet() {	 
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		    	   
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
			if (runDelete!=null) {
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}	 
			ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code",getNewReadSet().code));
			if (readSetDelete!=null) {
				MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
			}	
			
			Run run = getFullRun();
			ArrayList<Lane> l = new ArrayList<Lane>();
			 Lane lane = getLane();
			 lane.state = getState("F");
			 ReadSet r = getNewReadSet();
			 r.runCode = run.code;
			 r.laneNumber = 1;
			 ArrayList<String> a = new ArrayList<String>();
			 //a.add(r.code);			 
			 lane.readSetCodes = a;
			 l.add(lane);
			 run.lanes = l;

			 ContextValidation ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 run.validate(ctxVal);
			 assertThat(ctxVal.errors).hasSize(0);
			 
			 ctxVal.putObject("run", run);
			 ctxVal.setCreationMode();
			 run.lanes.get(0).validate(ctxVal);
			 assertThat(ctxVal.errors).hasSize(0);
			 
			 // the run must be saved to validate one of his readset 
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
			assertThat(status(result)).isEqualTo(OK);
				 
			 ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 r.validate(ctxVal);
			 assertThat(ctxVal.errors).hasSize(0);
			 
			result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(r)));
			assertThat(status(result)).isEqualTo(OK);
					  
			 a.add(r.code);
			 run.lanes.get(0).readSetCodes = a;
			 
			 ctxVal = new ContextValidation();
			 ctxVal.putObject("run", run);
			 ctxVal.setUpdateMode();
			 run.lanes.get(0).validate(ctxVal);
			 assertThat(ctxVal.errors).hasSize(0);
			 
			 ctxVal = new ContextValidation();
			 ctxVal.setCreationMode();
			 r.validate(ctxVal);
			 assertThat(ctxVal.errors).hasSize(1); // error.codenotunique [CORE_R1]		 
			 assertThat(ctxVal.errors.toString()).contains("CORE_R1");
		   }});
	 }
	 
	 
	 @Test
	 public void testFileValidationOK(){
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET21"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
			ReadSet readsetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code","test1"));
			if(readsetDelete!=null){
				MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readsetDelete._id);
			}
		
			Run run = RunMockHelper.newRun("YANN_TEST1FORREADSET21");
			run.dispatch = true; // For the archive test
			Lane lane = RunMockHelper.newLane(1);
			
			List<File> files =  new ArrayList<File>();
			File file = RunMockHelper.newFile("xxE410_FA_B00FPMH_3_2_C17GFACXX.IND6.fastq");
			files.add(file);

			ReadSet r = RunMockHelper.newReadSet("test1");
			r.files = files;
			
			ArrayList<String> a = new ArrayList<String>();
			a.add(r.code);
			lane.readSetCodes = a;
			Lane lane2 = RunMockHelper.newLane(2);
			List<Lane> lanes = new ArrayList<Lane>();
			lanes.add(lane);
			lanes.add(lane2);
			run.lanes = lanes;
			
			ContextValidation ctxVal = new ContextValidation();
			ctxVal.putObject("readSet", r);
			ctxVal.setCreationMode();
			file.validate(ctxVal);
			 
			assertThat(ctxVal.errors).hasSize(0);	
			   }});
			 
	 }
	
	
	 @Test
	 public void testFileValidationError(){
		 // add two identical files to the same readset
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET0"));
		if(runDelete!=null){
			MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
		ReadSet readsetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code","test1"));
		if(readsetDelete!=null){
			MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readsetDelete._id);
		}
	
		Run run = getFullRun();
		run.dispatch = true; // For the archive test
		Lane lane = RunMockHelper.newLane(1);
		List<File> files =  new ArrayList<File>();
		File file = RunMockHelper.newFile("xxE410_FA_B00FPMH_3_2_C17GFACXX.IND6.fastq");
		files.add(file);
		// AGAIN FOR ERROR
		files.add(file);

		ReadSet r = RunMockHelper.newReadSet("test1");
		r.files = files;
		r.runCode = run.code;
		r.laneNumber = 1;
		
		 ArrayList<String> a = new ArrayList<String>();
		 //a.add(r.code);
		lane.readSetCodes = a;
		Lane lane2 = RunMockHelper.newLane(2);
		List<Lane> lanes = new ArrayList<Lane>();
		lanes.add(lane);
		lanes.add(lane2);
		run.lanes = lanes;	
		
		 ContextValidation ctxVal = new ContextValidation();
		 ctxVal.putObject("readSet", r);
		 ctxVal.setCreationMode();
		 file.validate(ctxVal);
		 assertThat(ctxVal.errors).hasSize(0);
		 
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
			assertThat(status(result)).isEqualTo(OK);
		 
		 ctxVal = new ContextValidation();
		 ctxVal.setCreationMode();
		 r.validate(ctxVal);
		 
		 assertThat(ctxVal.errors).hasSize(1);
		 assertThat(ctxVal.errors.toString()).contains("files[1].fullname");
			   }});
	 }
	
	
	 
	private Lane getLane() {
		
		Lane lane = new Lane();
		lane.number = 1;
		 lane.state = getState("F");
		
		lane.validation = RunMockHelper.getValidation(TBoolean.TRUE);
		lane.readSetCodes = null;
		return lane;
	}
	
	private Lane getEmptyLane(){
		
		Lane lane = new Lane();
		lane.number = 0;
		List<String> r = new ArrayList<String>();
		r.add("X"); 
		lane.readSetCodes = r;
		 lane.state = getState("F");
		return lane;
	}
	
	private Lane getLane2(){
		
		Lane lane = new Lane();
		lane.number = 3;
		 lane.state = getState("F");
		return lane;
	}
	
	private ReadSet getNewReadSet() {
		ReadSet readSet = new ReadSet();
		readSet.code = "CORE_R1";
		readSet.path = "/path/test";
		readSet.projectCode = "PCODE"; 
		readSet.sampleCode = "SPCODE";
		readSet.sampleContainerCode = "SPCONTCODE";
		readSet.state = getState("F-QC");
		readSet.traceInformation = new TraceInformation();
		readSet.traceInformation.setTraceInformation("ngsrg");
		readSet.laneNumber = 1;
		readSet.dispatch = true;
		
		
		readSet.typeCode = "default-readset";
		
		return readSet;
	}
	
	private ReadSet getEmptyReadSet() {
		ReadSet readSet = new ReadSet();
		readSet.code = "X";
		readSet.path = "";
		readSet.projectCode = ""; 
		readSet.sampleCode = "";
		readSet.sampleContainerCode = "";
		readSet.state = getState("F-QC");
		readSet.typeCode = "default-readset";
		TraceInformation ti = new TraceInformation();
		ti.createUser = "dnoisett";
		ti.creationDate = new Date(); 		
		readSet.traceInformation = new TraceInformation(); 

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
		file.state = getState("A");
		return file;
	}
	  
	private Run getFullRun() {
			Run run = new Run();
			run.code = "YANN_TEST1FORREADSET0";
			run.containerSupportCode = "FC00000";
			run.dispatch = true;
			run.instrumentUsed = new InstrumentUsed();
			run.instrumentUsed.code = "HS7";
			run.instrumentUsed.categoryCode = "HISEQ2000";
			run.typeCode = "RHS2000";
			List<String> lResos = new ArrayList<String>();
			lResos.add("reso1");
			lResos.add("reso2");
			State state = new State();
			run.state = state;
			run.state.code = "F";
			run.state.user = "tests";
			run.state.date = new Date();
			run.traceInformation = new TraceInformation();
			run.traceInformation.setTraceInformation("test");
			Map<String, Treatment> lT = new HashMap<String, Treatment>();
			Treatment ngsrg = new Treatment(); 
			lT.put("ngsrg", ngsrg);
			ngsrg.categoryCode = "ngsrg";
			ngsrg.typeCode = "ngsrg-illumina";
			run.typeCode = "RHS2000";
			run.validation = new Validation();
			run.validation.user = "test";
			run.validation.valid = TBoolean.TRUE;
			run.validation.date = new Date(); 

			return run;
		}
	
	private Run getEmptyRun() {
		Run run = new Run();
		run.code = "";
		run.containerSupportCode = "";
		run.dispatch = null;
		run.instrumentUsed = new InstrumentUsed();
		run.instrumentUsed.code = "";
		run.instrumentUsed.categoryCode = "";
		run.typeCode = "";
		List<String> lResos = new ArrayList<String>();
		State state = new State();
		run.state = state;
		run.state.code = "";
		run.state.user = "tests";
		run.state.date = new Date();
		run.traceInformation = new TraceInformation();
		Map<String, Treatment> lT = new HashMap<String, Treatment>();
		Treatment ngsrg = new Treatment(); 
		lT.put("ngsrg", ngsrg);
		ngsrg.categoryCode = "";
		ngsrg.typeCode = "";
		run.typeCode = "";
		run.validation = new Validation();
		
	
		return run;
	}
}
