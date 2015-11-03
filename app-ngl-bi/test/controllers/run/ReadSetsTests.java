package controllers.run;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.project.instance.Project;
import models.laboratory.run.description.RunType;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongojack.DBQuery;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import utils.AbstractTestsCNG;
import utils.RunMockHelper;
import fr.cea.ig.MongoDBDAO;

public class ReadSetsTests extends AbstractTestsCNG {

	static Container c;
	Run run;
	ReadSet readset;
	ReadSet readset2;
	Lane lane;
	
	@BeforeClass
	public static void initData() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		ContainerSupport cs = new ContainerSupport();
		cs.code = "containerName";
		cs.categoryCode = "lane";

		MongoDBDAO.save(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, cs);

		Container c = new Container();
		c.code ="containerTest1";
		c.support = new LocationOnContainerSupport(); 
		c.support.code = cs.code; 

		MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME, c);

		Sample sample = RunMockHelper.newSample("SampleCode");
		Project project = RunMockHelper.newProject("ProjectCode");

		MongoDBDAO.save(InstanceConstants.SAMPLE_COLL_NAME, sample);
		MongoDBDAO.save(InstanceConstants.PROJECT_COLL_NAME, project);

	}


	@AfterClass
	public static void deleteData(){
		List<ContainerSupport> containerSupports = MongoDBDAO.find(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class).toList();
		for (ContainerSupport cs : containerSupports) {
			if (cs.code.equals("containerName")) {
				MongoDBDAO.delete(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, cs);
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

		Project project = MongoDBDAO.findOne(InstanceConstants.PROJECT_COLL_NAME, Project.class, DBQuery.is("code","ProjectCode"));
		if (project!= null) {
			MongoDBDAO.delete(InstanceConstants.PROJECT_COLL_NAME, Project.class, project._id);
		}
	}

	@Before
	public void createData()
	{
		// create a run with two readsets associated to this run
		run = RunMockHelper.newRun("YANN_TEST1FORREADSET");
		lane = RunMockHelper.newLane(1);
		Lane lane2 = RunMockHelper.newLane(2);
		List<Lane> lanes = new ArrayList<Lane>();

		readset = RunMockHelper.newReadSet("rdCode");
		readset.runCode = run.code;
		lanes.add(lane);
		lanes.add(lane2);
		run.lanes = lanes;
		
		readset2 = RunMockHelper.newReadSet("rdCode2");
		readset2.runCode = run.code;
	}

	@After
	public void removeData()
	{
		Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1FORREADSET"));
		if(runDelete!=null){
			MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
		ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
		if(readSetDelete!=null){
			MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,readSetDelete._id);
		}
		readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode2"));
		if(readSetDelete!=null){
			MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
		}
	}

	@Test
	public void testReadSetsCreate() { 
		// create a run with two readsets associated to this run
		run.dispatch = true; // For the archive test
		
		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		assertThat(status(result)).isEqualTo(OK);

		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
		assertThat(status(result)).isEqualTo(OK);

		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset2)));
		assertThat(status(result)).isEqualTo(OK);

		//query for control
		List<ReadSet> lr = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.in("code","rdCode", "rdCode2")).toList();
		assertThat(lr.size()).isEqualTo(2); 
	}


	@Test
	public void testArchiveReadSet() {
		run.dispatch = true; 
		
		Result result =callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		assertThat(status(result)).isEqualTo(OK);

		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
		assertThat(status(result)).isEqualTo(OK);

		result = callAction(controllers.archives.api.routes.ref.ReadSets.save(readset.code),fakeRequest().withJsonBody(RunMockHelper.getArchiveJson("codeTestArchive")));
		assertThat(status(result)).isEqualTo(OK);

		result = callAction(controllers.archives.api.routes.ref.ReadSets.save("ReadSetTESTNOTEXIT"),fakeRequest().withJsonBody(RunMockHelper.getArchiveJson("codeTestArchive")));
		assertThat(status(result)).isEqualTo(NOT_FOUND);

		//query for control
		ReadSet r = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code",readset.code));
		assertThat(r).isNotNull();
		assertThat(r.archiveId).isNotNull(); //means that this is a archive
	}


	@Test
	public void testAchiveList(){
		run.dispatch = true; 

		readset.laneNumber = 1;
		readset.dispatch = true;

		readset2.laneNumber = 1;
		readset2.dispatch = false;

		Result result =callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		assertThat(status(result)).isEqualTo(OK);

		//save readset
		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
		assertThat(status(result)).isEqualTo(OK);

		//save readset2 
		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset2)));
		assertThat(status(result)).isEqualTo(OK);

		// result = callAction(controllers.archives.api.routes.ref.ReadSets.list(),fakeRequest());
		// assertThat(status(result)).isEqualTo(OK);
		Logger.debug(contentAsString(result));
		assertThat(contentAsString(result)).isNotEqualTo("[]").contains(readset.code);
	}


	@Test
	public void testDeleteReadsets(){
		run.traceInformation = new TraceInformation();
		run.dispatch = true; // For the archive test

		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		assertThat(status(result)).isEqualTo(OK);

		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
		assertThat(status(result)).isEqualTo(OK);

		ArrayList<String> sCodes = new ArrayList<String>();
		sCodes.add(readset.code);
		lane.readSetCodes = sCodes; 
		result = callAction(controllers.runs.api.routes.ref.Lanes.update(run.code, lane.number),fakeRequest().withJsonBody(RunMockHelper.getJsonLane(lane)));
		assertThat(status(result)).isEqualTo(OK); 

		result = callAction(controllers.readsets.api.routes.ref.ReadSets.deleteByRunCode(run.code),fakeRequest());
		assertThat(status(result)).isEqualTo(OK);

		//query for control
		run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
		assertThat(run.lanes.size()).isEqualTo(2); 
		boolean b = (run.lanes.get(0).readSetCodes == null) || (run.lanes.get(0).readSetCodes.size() == 0); 
		assertThat(b).isEqualTo(true);

		//b = MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("runCode",run.code));
		//assertThat(b).isEqualTo(false);  
	}


	@Test 
	public void testRemoveReadset(){

		run.state=null;
		run.dispatch = true; // For the archive test
		
		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		Logger.info(contentAsString(result));
		assertThat(status(result)).isEqualTo(OK);

		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
		assertThat(status(result)).isEqualTo(OK);

		result = callAction(controllers.readsets.api.routes.ref.ReadSets.delete(readset.code),fakeRequest());
		assertThat(status(result)).isEqualTo(OK);	

		//query for control
		run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
		assertThat(run.lanes.size()).isEqualTo(2);
		assertThat(run.lanes.get(0).readSetCodes).isEmpty(); 

		readset = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("runCode",run.code));
		assertThat(readset).isNull(); 
	}

	/**
	 * Test impossible du à la generation des getters/setters de play
	 */
	//@Test
	public void shouldUpdatePathReadSet()
	{
		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		Logger.info(contentAsString(result));
		assertThat(status(result)).isEqualTo(OK);
		
		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
		assertThat(status(result)).isEqualTo(OK);

		
		ReadSet readSetData = new ReadSet();
		readSetData.path="expectedPath";
		Logger.info("READ SET "+Json.toJson(readSetData).toString());
		result = callAction(controllers.readsets.api.routes.ref.ReadSets.update("rdCode"),fakeRequest("PUT","?fields=path").withJsonBody(Json.toJson(readSetData)));
		Logger.info(contentAsString(result));
		assertThat(status(result)).isEqualTo(OK);
		
		//Check path of readSet
		readset = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, "rdCode");
		Assert.assertNotNull(readset);
		Assert.assertEquals("expectedPath", readset.path);
	}

}
