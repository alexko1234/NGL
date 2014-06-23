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
import java.util.List;

import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import org.mongojack.DBQuery;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Logger;
import play.mvc.Result;
import utils.AbstractTests;
import utils.RunMockHelper;
import fr.cea.ig.MongoDBDAO;

public class RunsTests extends AbstractTests {
	
	static Container c;
	
	@BeforeClass
	public static void initData() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		ContainerSupport cs = new ContainerSupport();
		cs.code = "containerName";
		cs.categoryCode = "lane";
		   
		MongoDBDAO.save(InstanceConstants.SUPPORT_COLL_NAME, cs);
		
	   Container c = new Container();
	   c.code ="containerTest1";
	   c.support = new LocationOnContainerSupport(); 
	   c.support.code = cs.code; 
	   
	   MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME, c);
	}
	
	
	@AfterClass
	public static void deleteData(){
		List<ContainerSupport> containerSupports = MongoDBDAO.find(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class).toList();
		for (ContainerSupport cs : containerSupports) {
			if (cs.code.equals("containerName")) {
				MongoDBDAO.delete(InstanceConstants.SUPPORT_COLL_NAME, cs);
			}
		}
		List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList();
		for (Container container : containers) {
			MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME, container);
		}
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class).toList();
		for (Sample sample : samples) {
			MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, sample);
		}
	}
	
	@Test
	public void testRunSave() { 
		Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
		if(runDelete!=null){
			MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
		ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code","ReadSet00"));
		if(readSetDelete!=null){
			MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
		}	
		Sample sample = MongoDBDAO.findOne(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("code","SampleCode"));
		if (sample!= null) {
			MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,sample._id);
		}
		Project project = MongoDBDAO.findOne(InstanceConstants.PROJECT_COLL_NAME, Project.class, DBQuery.is("code","ProjectCode"));
		if (project!= null) {
			MongoDBDAO.delete(InstanceConstants.PROJECT_COLL_NAME, Project.class, project._id);
		}
		
		sample = RunMockHelper.newSample("SampleCode");
		project = RunMockHelper.newProject("ProjectCode");
		
		MongoDBDAO.save(InstanceConstants.SAMPLE_COLL_NAME, sample);
		MongoDBDAO.save(InstanceConstants.PROJECT_COLL_NAME, project);
	
		Run run = RunMockHelper.newRun("YANN_TEST1");
		Lane lane = RunMockHelper.newLane(1);
				
		List<Lane> lanes = new ArrayList<Lane>();
		lanes.add(lane);
		run.lanes = lanes;

		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		assertThat(status(result)).isEqualTo(OK);		
        
		ReadSet r = RunMockHelper.newReadSet("ReadSet00");		
		r.runCode = run.code;
		r.laneNumber = lane.number;
        result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(r)));
        assertThat(status(result)).isEqualTo(OK);
        
	    //query for control
        run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
        assertThat(run.lanes.size()).isEqualTo(1);
        assertThat(run.lanes.get(0).number).isEqualTo(1);
        
        r = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code",r.code));
        assertThat(r.code).isEqualTo("ReadSet00");
        assertThat(r.runCode).isEqualTo(run.code);
        assertThat(r.laneNumber).isEqualTo(lane.number);
    }

	@Test
	public void testRunUpdate() { 
		// change the run dispatch value : false to true		
		Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
		if(runDelete!=null){
			MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
			
		Run run = RunMockHelper.newRun("YANN_TEST1");
		Lane lane = RunMockHelper.newLane(1);
		List<Lane> lanes = new ArrayList<Lane>();
		lanes.add(lane);
		run.lanes = lanes;
		
		run.dispatch=false;
		
	 	Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	 	assertThat(status(result)).isEqualTo(OK);
        
        run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
        assertThat(run.dispatch).isEqualTo(false);
        
        run.dispatch=true;
		result = callAction(controllers.runs.api.routes.ref.Runs.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	 	Logger.debug(contentAsString(result));
	 	assertThat(status(result)).isEqualTo(OK);
	 	
	 	//query for control
	 	run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
	 	assertThat(run.dispatch).isEqualTo(true);
	}
	
	@Test
	public void testRunSaveWithTwiceSameReadSet() {
		Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST2"));
		if(runDelete!=null){
			MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
		ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code","ReadSet2"));
		if(readSetDelete!=null){
			MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
		}	
		Sample sample = MongoDBDAO.findOne(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("code","SampleCode"));
		if (sample!= null) {
			MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,sample._id);
		}
		Project project = MongoDBDAO.findOne(InstanceConstants.PROJECT_COLL_NAME, Project.class, DBQuery.is("code","ProjectCode"));
		if (project!= null) {
			MongoDBDAO.delete(InstanceConstants.PROJECT_COLL_NAME, Project.class, project._id);
		}
		
		sample = RunMockHelper.newSample("SampleCode");
		project = RunMockHelper.newProject("ProjectCode");
		
		MongoDBDAO.save(InstanceConstants.SAMPLE_COLL_NAME, sample);
		MongoDBDAO.save(InstanceConstants.PROJECT_COLL_NAME, project);
		
		Run run = RunMockHelper.newRun("YANN_TEST2");
		Lane lane = RunMockHelper.newLane(1);
		List<Lane> lanes = new ArrayList<Lane>();
		lanes.add(lane);
		run.lanes = lanes;
	
		ReadSet r = RunMockHelper.newReadSet("ReadSet2");
		r.runCode = run.code;
		r.laneNumber = lane.number;
		
		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		assertThat(status(result)).isEqualTo(OK);
    
		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(r)));
        assertThat(status(result)).isEqualTo(OK);
        
        // update masterReadSetCodes
		List<String> a = new ArrayList<String>();
		a.add(r.code);
		a.add(r.code);
		run.lanes.get(0).readSetCodes = a;
		
		//insert run with the readset r twice
		result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
        assertThat(status(result)).isEqualTo(play.mvc.Http.Status.BAD_REQUEST);			
        
		result = callAction(controllers.runs.api.routes.ref.Runs.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
        assertThat(status(result)).isEqualTo(play.mvc.Http.Status.BAD_REQUEST);	
        
	 	//query for control
	 	run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
	 	assertThat(run.lanes.get(0).readSetCodes.size()).isEqualTo(1);
	 	assertThat(run.lanes.get(0).readSetCodes.get(0)).isEqualTo(r.code);
	}
	
	@Test
	public void testPropertyLaneUpdate() {
		// verify that the property "valid" of the lane is update to TRUE
		Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
		if(runDelete!=null){
			MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
		Run run = RunMockHelper.newRun("YANN_TEST1");
		Lane lane = RunMockHelper.newLane(1);
		List<Lane> lanes = new ArrayList<Lane>();
		lanes.add(lane);
		run.lanes = lanes;
		
		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		assertThat(status(result)).isEqualTo(OK);
        
		lane.valuation = RunMockHelper.getValuation(TBoolean.TRUE);
		
		result = callAction(controllers.runs.api.routes.ref.Lanes.update(run.code, lane.number),fakeRequest().withJsonBody(RunMockHelper.getJsonLane(lane)));
		assertThat(status(result)).isEqualTo(OK);
				
	 	//query for control
	 	run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
	 	assertThat(lane.valuation.valid).isEqualTo(TBoolean.TRUE);
	}
	

	@Test
	public void testPropertyReadSetUpdate() {
		// verify that the property "dispatch" of the readSet is update to false
		Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
		if(runDelete!=null){
			MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
		ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code","ReadSet01"));
		if(readSetDelete!=null){
			MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
		}
		Sample sample = MongoDBDAO.findOne(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("code","SampleCode"));
		if (sample!= null) {
			MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,sample._id);
		}
		Project project = MongoDBDAO.findOne(InstanceConstants.PROJECT_COLL_NAME, Project.class, DBQuery.is("code","ProjectCode"));
		if (project!= null) {
			MongoDBDAO.delete(InstanceConstants.PROJECT_COLL_NAME, Project.class, project._id);
		}
		
		sample = RunMockHelper.newSample("SampleCode");
		project = RunMockHelper.newProject("ProjectCode");
		
		MongoDBDAO.save(InstanceConstants.SAMPLE_COLL_NAME, sample);
		MongoDBDAO.save(InstanceConstants.PROJECT_COLL_NAME, project);
		
		Run run = RunMockHelper.newRun("YANN_TEST1");
		Lane lane = RunMockHelper.newLane(1);
		List<Lane> lanes = new ArrayList<Lane>();
		lanes.add(lane);
		run.lanes = lanes;

		ReadSet r = RunMockHelper.newReadSet("ReadSet01");
		r.runCode = run.code;
		r.laneNumber = lane.number;
		
		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		assertThat(status(result)).isEqualTo(OK);
		
		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(r)));
		assertThat(status(result)).isEqualTo(OK);
		
        r = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code",r.code));
        assertThat(r._id).isNotEqualTo(null);

        r.dispatch = false;
		
		result = callAction(controllers.readsets.api.routes.ref.ReadSets.update(r.code),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(r)));
        assertThat(status(result)).isEqualTo(OK);

	 	//query for control
        r = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code",r.code));
        assertThat(r.dispatch).isEqualTo(false);
	}
	
	@Test
	public void testDeleteRun(){
		Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
		if(runDelete!=null){
			MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
		Run run = RunMockHelper.newRun("YANN_TEST1");
		Lane lane = RunMockHelper.newLane(1);
		List<Lane> lanes = new ArrayList<Lane>();
		lanes.add(lane);
		run.lanes = lanes;
		
		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		assertThat(status(result)).isEqualTo(OK);

		result = callAction(controllers.runs.api.routes.ref.Runs.delete(run.code),fakeRequest());
		assertThat(status(result)).isEqualTo(OK);
		
	 	//query for control
	 	run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
	 	assertThat(run).isNull();	
	}

	

}
