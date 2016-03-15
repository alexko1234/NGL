package controllers.run;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.Institute;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.project.instance.Project;
import models.laboratory.resolutions.instance.Resolution;
import models.laboratory.resolutions.instance.ResolutionCategory;
import models.laboratory.resolutions.instance.ResolutionConfiguration;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.SampleOnContainer;
import models.laboratory.sample.description.ImportCategory;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import utils.AbstractTestsCNG;
import utils.RunMockHelper;

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

		ResolutionConfiguration resoConfig = new ResolutionConfiguration();
		resoConfig.code="conf-reso-run";
		resoConfig.objectTypeCode="Run";
		resoConfig.resolutions=new ArrayList<>();
		resoConfig.typeCodes=new ArrayList<>();
		resoConfig.typeCodes.add("RHS2000");
		ResolutionCategory resoCategory = new ResolutionCategory();
		resoCategory.name="Info";
		Resolution reso1 = new Resolution();
		reso1.code="reso1";
		reso1.name="reso1";
		reso1.displayOrder=1;
		reso1.level="default";
		reso1.category=resoCategory;
		resoConfig.resolutions.add(reso1);
		Resolution reso2 = new Resolution();
		reso2.code="reso2";
		reso2.name="reso2";
		reso2.displayOrder=2;
		reso2.level="default";
		reso2.category=resoCategory;
		resoConfig.resolutions.add(reso2);
		MongoDBDAO.save(InstanceConstants.RESOLUTION_COLL_NAME,resoConfig);


		resoConfig = new ResolutionConfiguration();
		resoConfig.code="conf-reso-readset";
		resoConfig.objectTypeCode="ReadSet";
		resoConfig.resolutions=new ArrayList<>();
		resoConfig.typeCodes=new ArrayList<>();
		resoConfig.typeCodes.add("default-readset");
		resoConfig.resolutions.add(reso1);
		resoConfig.resolutions.add(reso2);
		MongoDBDAO.save(InstanceConstants.RESOLUTION_COLL_NAME,resoConfig);
		
		/*List<Institute> institutes = new ArrayList<>();
		institutes.add(Institute.find.findByCode("CNS"));
		ImportType it = new ImportType();
		it.code = "external";
		it.name = "External";
		it.category = ImportCategory.find.findByCode("sample-import");
		it.objectType = ObjectType.find.findByCode(ObjectType.CODE.Import.name());
		it.propertiesDefinitions = new ArrayList<>();
		it.institutes = institutes;
		it.states = State.find.findByObjectTypeCode(ObjectType.CODE.Sample); 
		it.save();*/
		
	}


	@AfterClass
	public static void deleteData(){
		ContainerSupport containerSupport = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, "containerName");
		if (containerSupport!=null)
			MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, containerSupport.code);

		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, "containerTest1");
		if(container!=null)
			MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code);

		//Sample sample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, "SampleCode");
		//if(sample!=null)
		MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, "SampleCode");
		
		MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, "newSample");

		//Project project = MongoDBDAO.findByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, "ProjectCode");
		//if (project!= null) {
		MongoDBDAO.deleteByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, "ProjectCode");
		//}
		
		ResolutionConfiguration resolutionConfig = MongoDBDAO.findByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, "conf-reso-run");
		if(resolutionConfig!=null)
			MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, "conf-reso-run");

		resolutionConfig = MongoDBDAO.findByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, "conf-reso-readset");
		if(resolutionConfig!=null)
			MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, "conf-reso-readset");
		
		//ImportType it = ImportType.find.findByCode("external");
		//it.remove();
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
		Logger.debug(contentAsString(result));
		assertThat(status(result)).isEqualTo(OK);

		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
		Logger.debug(contentAsString(result));
		assertThat(status(result)).isEqualTo(OK);

		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset2)));
		Logger.debug(contentAsString(result));
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
	
	/*@Test
	public void testCreateReadSetExternalData()
	{
		//ReadSet external with sampleOnContainer and sample not in database
		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		Logger.debug(contentAsString(result));
		assertThat(status(result)).isEqualTo(OK);

		//Check sample doesnt exist
		Sample sample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, "newSample");
		assertThat(sample==null);
		readset.sampleCode="newSample";
		SampleOnContainer sampleOnContainer = RunMockHelper.newSampleOnContainer("newSample");
		readset.sampleOnContainer=sampleOnContainer;
		readset.sampleOnContainer.sampleCategoryCode="DNA";
		readset.sampleOnContainer.sampleTypeCode="gDNA";
		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
		Logger.debug(contentAsString(result));
		assertThat(status(result)).isEqualTo(OK);
		
		//Check sample exist
		sample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, "newSample");
		assertThat(sample!=null);
	}*/
	
	@Test
	public void testSaveReadSetWithSampleOnContainerExternal()
	{
		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		Logger.debug(contentAsString(result));
		assertThat(status(result)).isEqualTo(OK);

		SampleOnContainer sampleOnContainer = RunMockHelper.newSampleOnContainer("newSample");
		readset.sampleOnContainer=sampleOnContainer;
		
		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest(play.test.Helpers.POST, "?external=true").withJsonBody(RunMockHelper.getJsonReadSet(readset)));
		Logger.debug(contentAsString(result));
		assertThat(status(result)).isEqualTo(OK);

		
	}
	
	@Test
	public void testSaveReadSetWithoutSampleOnContainerExternal()
	{
		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		Logger.debug(contentAsString(result));
		assertThat(status(result)).isEqualTo(OK);

		readset.sampleOnContainer=null;
		
		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest(play.test.Helpers.POST, "?external=true").withJsonBody(RunMockHelper.getJsonReadSet(readset)));
		Logger.debug(contentAsString(result));
		assertThat(status(result)).isEqualTo(BAD_REQUEST);
		assertThat(contentAsString(result).contains("sampleOnContainer"));
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
