package controllers.run;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.AbstractDocument.Content;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.Level.CODE;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.run.description.TreatmentType;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Logger;
import play.mvc.Result;
import utils.AbstractTests;
import utils.RunMockHelper;
import validation.ContextValidation;
import validation.run.instance.TreatmentValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class LaneTreatmentsTests_Beta extends AbstractTests {
	
	static Container c;
	
	@BeforeClass
	public static void initData() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		   Container c = new Container();
		   c.code ="containerTest1";
		   c.support = new ContainerSupport(); 
		   c.support.barCode = "containerName"; 
		   
		   MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME, c);
		       }}); 
	}
	
	
	@AfterClass
	public static void deleteData(){
		running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class).toList();
		for (Sample sample : samples) {
			MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, sample);
		}
		List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList();
		for (Container container : containers) {
			MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME, container);
		}
		       }}); 
	}
	
	private Run createRunWithLaneCode() {
		Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","DIDIER_TESTFORTRT"));
		if(runDelete!=null){
			MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
		Run run = RunMockHelper.newRun("DIDIER_TESTFORTRT");
		run.dispatch = true; // For the archive test
		Lane lane = RunMockHelper.newLane(1);
		Lane lane2 = RunMockHelper.newLane(2);
		List<Lane> lanes = new ArrayList<Lane>();		
		lanes.add(lane);
		lanes.add(lane2);
		run.lanes = lanes;
		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		assertThat(status(result)).isEqualTo(OK);
		
		return run;
	}
	
	private Treatment getNewTreatmentForLane() {
		Treatment t = new Treatment();
		t.code =  "ngsrg";		
		t.typeCode = "ngsrg-illumina";
		t.categoryCode = "ngsrg";
		//define map of single property values
		Map<String,PropertyValue> m = new HashMap<String,PropertyValue>();
		m.put("nbCluster", new PropertySingleValue(100000));
		m.put("prephasing", new PropertySingleValue("OK"));
		m.put("nbClusterInternalAndIlluminaFilter", new PropertySingleValue(75894));
		m.put("phasing", new PropertySingleValue("OK"));
		m.put("nbCycleReadIndex2", new PropertySingleValue(100));
		m.put("nbCycleRead2", new PropertySingleValue(100));
		m.put("nbCycleRead1", new PropertySingleValue(100));
		m.put("nbCycleReadIndex1", new PropertySingleValue(100));
		m.put("nbBaseInternalAndIlluminaFilter", new PropertySingleValue(153654));
		m.put("nbClusterInternalAndIlluminaFilter", new PropertySingleValue(546723));
		//m.put("seqLossPercent", new PropertySingleValue(96.125)); // not required
		//new 
		m.put("nbClusterIlluminaFilter", new PropertySingleValue(961250));
		m.put("percentClusterIlluminaFilter", new PropertySingleValue(96.125));
		m.put("percentClusterInternalAndIlluminaFilter", new PropertySingleValue(96.125));
		
		t.set("default", m);
		
		return t;
	}
   
	
	
	@Test
	public void testSave() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		     public void run() {
		    createRunWithLaneCode();
			    	 
			Treatment t = getNewTreatmentForLane();
			
			Result result = callAction(controllers.runs.api.routes.ref.LaneTreatments.save("DIDIER_TESTFORTRT", 1),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
			Logger.debug(contentAsString(result));
			assertThat(status(result)).isEqualTo(OK);
			
			//query for control
	        Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code","DIDIER_TESTFORTRT"));
	        assertThat(r.lanes.get(0).treatments.size()).isEqualTo(1);
	        Map.Entry<String, Treatment> entry = r.lanes.get(0).treatments.entrySet().iterator().next();
	        assertThat(entry.getKey()).isEqualTo("ngsrg");
		 }});
	}
	
	
	@Test
	public void testUpdate() { 
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		     public void run() {
		    createRunWithLaneCode();
			    	 
			Treatment t = getNewTreatmentForLane();
			
			Result result = callAction(controllers.runs.api.routes.ref.LaneTreatments.save("DIDIER_TESTFORTRT", 1),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
			assertThat(status(result)).isEqualTo(OK);
			
			// modify values of t
			Map<String,PropertyValue> m2 = new HashMap<String,PropertyValue>();
			
			ContextValidation ctx = new ContextValidation(); 
			TreatmentType treatmentType = TreatmentValidationHelper.validateRequiredDescriptionCode(ctx, t.typeCode, "typeCode", TreatmentType.find,true); 
			
			Map<String, PropertyValue> props = t.results.get(treatmentType.contexts.get(0).code);
			List<PropertyDefinition> propertyDefinitions = treatmentType.getPropertyDefinitionByLevel(Level.CODE.valueOf(treatmentType.contexts.get(0).name), Level.CODE.Lane); 
			
			for (Map.Entry<String, PropertyValue> e : t.results().get("default").entrySet()) {
				for (PropertyDefinition pDef : propertyDefinitions) {
					if (pDef.code.equals(e.getKey())) {
						if (pDef.propertyType.equals("String")) {
							e.getValue().value = "KO";	
						}
						else {
							if (pDef.propertyType.equals("Boolean")) {
								e.getValue().value = false;
							}
							else {
								//number
								e.getValue().value = 10;
							}
						}
					}
				}
				m2.put(e.getKey().toString(), new PropertySingleValue(e.getValue().value));
			}
			
			t.results().remove("default");
			t.set("default", m2);
			
			result = callAction(controllers.runs.api.routes.ref.LaneTreatments.update("DIDIER_TESTFORTRT", 1, t.code),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
			assertThat(status(result)).isEqualTo(OK);
			
			//query for control
	        Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code","DIDIER_TESTFORTRT"));
	        assertThat(r.lanes.get(0).treatments.size()).isEqualTo(1);
	        Map.Entry<String, Treatment> entry = r.lanes.get(0).treatments.entrySet().iterator().next();
	        assertThat(entry.getKey()).isEqualTo("ngsrg");
	        assertThat(entry.getValue().results().get("default").get("nbCycleReadIndex1").value.toString()).isEqualTo("10");
		}});
	}
	
	
	
	@Test
	public void testDelete() { 
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		     public void run() {
		    createRunWithLaneCode();
			    	 
			Treatment t = getNewTreatmentForLane();
			
			Result result = callAction(controllers.runs.api.routes.ref.LaneTreatments.save("DIDIER_TESTFORTRT", 1),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
			assertThat(status(result)).isEqualTo(OK);
			
			
			result = callAction(controllers.runs.api.routes.ref.LaneTreatments.delete("DIDIER_TESTFORTRT", 1, t.code),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
			assertThat(status(result)).isEqualTo(OK);
			
			//query for control
	        Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code","DIDIER_TESTFORTRT"));
	        assertThat(r.lanes.get(0).treatments.size()).isEqualTo(0);
		}});
	}
	
	
	@Test
	public void testGet() { 
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		     public void run() {
		    Run run = createRunWithLaneCode();
			    	 
			Treatment t = getNewTreatmentForLane();
			
			Map<String, Treatment> m = new HashMap<String, Treatment>(); 
			m.put("ngsrg", t);
			run.lanes.get(0).treatments = m; 
			
			/*
			ContextValidation ctx = new ContextValidation();
			ctx.putObject("level", Level.CODE.Lane);
			ctx.putObject("run", run);
			ctx.putObject("lane", run.lanes.get(0));
			ctx.setCreationMode();
			t.validate(ctx);
			System.out.println(ctx.errors);
			assertThat(ctx.errors.size()).isEqualTo(0);
			*/
			
			Result result = callAction(controllers.runs.api.routes.ref.LaneTreatments.save("DIDIER_TESTFORTRT", 1),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
			assertThat(status(result)).isEqualTo(OK);
			
			result = callAction(controllers.runs.api.routes.ref.LaneTreatments.get("DIDIER_TESTFORTRT", 1, t.code),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
			assertThat(status(result)).isEqualTo(OK);
		}});		
	}
	
	
	@Test
	public void testHead() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		     public void run() {
		    createRunWithLaneCode();
			    	 
			Treatment t = getNewTreatmentForLane();
	
			Result result = callAction(controllers.runs.api.routes.ref.LaneTreatments.save("DIDIER_TESTFORTRT", 1),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
			assertThat(status(result)).isEqualTo(OK);
			
			result = callAction(controllers.runs.api.routes.ref.LaneTreatments.head("DIDIER_TESTFORTRT", 1, t.code),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
			assertThat(status(result)).isEqualTo(OK);
		}});	
	}
	
	
}
