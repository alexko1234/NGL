package controllers.run;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;

import org.junit.AfterClass;
import org.junit.Test;

import play.mvc.Result;
import utils.AbstractTests;
import utils.RunMockHelper;
import fr.cea.ig.MongoDBDAO;

public class RunTreatmentsTests extends AbstractTests {	
	
	@AfterClass
	public static void deleteData(){
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class).toList();
		for (Sample sample : samples) {
			MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, sample);
		}
	}
	
	private void createRunCode() {
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
	}
	
	private Treatment getNewTreatment() {
		Treatment t = new Treatment();
		t.code =  "ngsrg";		
		t.typeCode = "ngsrg-illumina";
		t.categoryCode = "ngsrg";
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
		t.set("default", m);
		
		return t;
	}
	
	
	@Test
	public void testSave() {	 
	    createRunCode();
	    	 
	    Treatment t = getNewTreatment();
		    	 
		Result result = callAction(controllers.runs.api.routes.ref.RunTreatments.save("DIDIER_TESTFORTRT"),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
		assertThat(status(result)).isEqualTo(OK);
		
		//query for control
        Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code","DIDIER_TESTFORTRT"));
        assertThat(r.treatments.size()).isEqualTo(1);
        Map.Entry<String, Treatment> entry = r.treatments.entrySet().iterator().next();
        assertThat(entry.getKey()).isEqualTo("ngsrg");
	}
	
	
	
	@Test
	public void testUpdate() {
	    createRunCode();
		    	 
	    Treatment t = getNewTreatment();
		
		Result result = callAction(controllers.runs.api.routes.ref.RunTreatments.save("DIDIER_TESTFORTRT"),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
		assertThat(status(result)).isEqualTo(OK);
		
		//define map of single property values
		Map<String,PropertyValue> m2 = new HashMap<String,PropertyValue>();
		m2.put("nbClusterTotal", new PropertySingleValue(299)); // valeur simple
		m2.put("nbClusterIlluminaFilter", new PropertySingleValue(200));
		m2.put("nbCycle", new PropertySingleValue(200));
		m2.put("nbBase", new PropertySingleValue(200));
		m2.put("flowcellPosition", new PropertySingleValue("B"));
		m2.put("rtaVersion", new PropertySingleValue("v2"));
		m2.put("flowcellVersion", new PropertySingleValue("v3"));
		m2.put("controlLane", new PropertySingleValue(66));
		m2.put("mismatch", new PropertySingleValue(false));
		
		t.results().remove("default");
		t.set("default", m2);
		
		result = callAction(controllers.runs.api.routes.ref.RunTreatments.update("DIDIER_TESTFORTRT", t.code),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
		assertThat(status(result)).isEqualTo(OK);
		
		//query for control
        Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code","DIDIER_TESTFORTRT"));
        assertThat(r.treatments.size()).isEqualTo(1);
        Map.Entry<String, Treatment> entry = r.treatments.entrySet().iterator().next();
        assertThat(entry.getKey()).isEqualTo("ngsrg");
        assertThat(entry.getValue().results().get("default").get("nbClusterTotal").value.toString()).isEqualTo("299");
	}
	
	
	
	@Test
	public void testDelete() { 
	    createRunCode();
		    	 
	    Treatment t =	 getNewTreatment();
		
		Result result = callAction(controllers.runs.api.routes.ref.RunTreatments.save("DIDIER_TESTFORTRT"),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
		assertThat(status(result)).isEqualTo(OK);
		
		
		result = callAction(controllers.runs.api.routes.ref.RunTreatments.delete("DIDIER_TESTFORTRT", t.code),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
		assertThat(status(result)).isEqualTo(OK);
		
		//query for control
        Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code","DIDIER_TESTFORTRT"));
        assertThat(r.treatments.size()).isEqualTo(0);
	}
	
	
	@Test
	public void testGet() {
		createRunCode();
		    	 
	    Treatment t =	 getNewTreatment();
		
		
		Result result = callAction(controllers.runs.api.routes.ref.RunTreatments.save("DIDIER_TESTFORTRT"),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
		assertThat(status(result)).isEqualTo(OK);
		
		result = callAction(controllers.runs.api.routes.ref.RunTreatments.get("DIDIER_TESTFORTRT", t.code),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
		assertThat(status(result)).isEqualTo(OK);
	}
	
	
	@Test
	public void testHead() {
	    createRunCode();
		    	 
	    Treatment t =	 getNewTreatment();
		
		Result result = callAction(controllers.runs.api.routes.ref.RunTreatments.save("DIDIER_TESTFORTRT"),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
		assertThat(status(result)).isEqualTo(OK);
		
		result = callAction(controllers.runs.api.routes.ref.RunTreatments.head("DIDIER_TESTFORTRT", t.code),fakeRequest().withJsonBody(RunMockHelper.getJsonTreatment(t)));
		assertThat(status(result)).isEqualTo(OK);
	}
	
}

