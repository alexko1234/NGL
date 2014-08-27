package workflows;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongojack.DBQuery;

import play.mvc.Result;
import utils.AbstractTests;
import utils.RunMockHelper;
import fr.cea.ig.MongoDBDAO;


public class StatesTests extends  AbstractTests {	
	
	static Container c;
	Run run;
	ReadSet readset;
	Sample sample;
	Project project;
	Result r1;
	Result r2;
	
	@BeforeClass
	public static void initData() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class).toList();
		for (Sample sample : samples) {
			MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, sample);
		}
		List<Project> projects = MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, Project.class).toList();
		for (Project project : projects) {
			MongoDBDAO.delete(InstanceConstants.PROJECT_COLL_NAME, project);
		}
		List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList();
		for (Container container : containers) {
			MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME, container);
		}
		List<ContainerSupport> containerSupports = MongoDBDAO.find(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class).toList();
		for (ContainerSupport cs : containerSupports) {
			if (cs.code.equals("containerName")) {
				MongoDBDAO.delete(InstanceConstants.SUPPORT_COLL_NAME, cs);
			}
		}
		List<Run> runs = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class).toList();
		for (Run run : runs) {
			MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);
		}		
		List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class).toList();
		for (ReadSet readSet : readSets) {
			MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet);
		}
		
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
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class).toList();
		for (Sample sample : samples) {
			MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, sample);
		}
		List<Project> projects = MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, Project.class).toList();
		for (Project project : projects) {
			MongoDBDAO.delete(InstanceConstants.PROJECT_COLL_NAME, project);
		}
		List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList();
		for (Container container : containers) {
			MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME, container);
		}
		List<ContainerSupport> containerSupports = MongoDBDAO.find(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class).toList();
		for (ContainerSupport cs : containerSupports) {
			if (cs.code.equals("containerName")) {
				MongoDBDAO.delete(InstanceConstants.SUPPORT_COLL_NAME, cs);
			}
		}
		List<Run> runs = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class).toList();
		for (Run run : runs) {
			MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);
		}		
		List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class).toList();
		for (ReadSet readSet : readSets) {
			MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet);
		}
	}
	
	
	public void prepareData() {
		
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
	
		run = RunMockHelper.newRun("YANN_TEST1");
		run.state.code = "N";
		Lane lane = RunMockHelper.newLane(1);
		List<Lane> lanes = new ArrayList<Lane>();
		lanes.add(lane);
		run.lanes = lanes;
        
		r1 = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));

		readset = RunMockHelper.newReadSet("ReadSet00");	
		readset.state.code = "N";
		readset.runCode = run.code;
		readset.laneNumber = lane.number;
        
		r2 = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
		
		List<String> readsetCodes = new ArrayList<String>();
		readsetCodes.add(readset.code);
		lane.readSetCodes = readsetCodes;  
	}

	
	

	@Test
	public void testPrepareData() {
		prepareData();
		assertThat(status(r1)).isEqualTo(OK);
		assertThat(status(r2)).isEqualTo(OK);
	}
	
	@Test
	public void testRunStateN() {
		prepareData();	
		assertThat(run.state.code).isEqualTo("N");
	}

	@Test
	public void testRunStateIPS() {
		 prepareData();		 
  
		run.state.code = "IP-S";
		run.state.user = "test";

		//controllers.runs.api.State.update
		r1 = callAction(controllers.runs.api.routes.ref.State.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(run.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));		
		assertThat(r.state.code).isEqualTo("IP-S");
	}  


	@Test
	public void testRunStateFES() {
		prepareData();
		
		run.state.code = "FE-S";
		run.state.user = "test";

		//controllers.runs.api.State.update
		r1 = callAction(controllers.runs.api.routes.ref.State.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(run.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
		assertThat(r.state.code).isEqualTo("FE-S");
	}
	
	
	@Test
	public void testRunStateFS() {
		prepareData();
		
		run.state.code = "F-S";
		run.state.user = "test";

		//controllers.runs.api.State.update
		r1 = callAction(controllers.runs.api.routes.ref.State.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(run.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
		assertThat(r.state.code).isEqualTo("IW-RG");
	}

	
	@Test
	public void testRunStateIPRG() {
		prepareData();

		run.state.code = "IP-RG";
		run.state.user = "test";

		r1 = callAction(controllers.runs.api.routes.ref.State.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(run.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
		assertThat(r.state.code).isEqualTo("IP-RG");
	}
	
	
	@Test
	public void testRunStateFRG() {
		prepareData();

		run.state.code = "F-RG";
		run.state.user = "test";

		r1 = callAction(controllers.runs.api.routes.ref.State.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(run.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
		assertThat(r.state.code).isEqualTo("IW-V");
	}
	
	
	 
	@Test
	public void testRunStateIPV() {
		prepareData();

		run.state.code = "IP-V";
		run.state.user = "test";

		r1 = callAction(controllers.runs.api.routes.ref.State.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(run.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
		assertThat(r.state.code).isEqualTo("IP-V");
	}

	
	@Test
	public void testRunStateFV() {
		prepareData();

		run.state.code = "F-V";
		run.state.user = "test";

		//make complete valuation
        run.valuation.valid = TBoolean.TRUE;
        for (Lane l : run.lanes) {
        	l.valuation.valid = TBoolean.TRUE;
        }

		r1 = callAction(controllers.runs.api.routes.ref.State.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(run.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
		assertThat(r.state.code).isEqualTo("IP-V");
	}

	

	@Test
	public void testRunStateStayToN() {
		prepareData();
		
		run.state.code = "N";
		run.state.user = "test";

		//make complete valuation
        run.valuation.valid = TBoolean.TRUE;
        for (Lane l : run.lanes) {
        	l.valuation.valid = TBoolean.TRUE;
        }
                
        run.lanes.get(0).valuation.valid = TBoolean.FALSE; // Valuation non completed
        
		r1 = callAction(controllers.runs.api.routes.ref.State.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(run.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
		assertThat(r.state.code).isEqualTo("N");
	}
	
	
	@Test
	public void testRunStateStayToIPV() {
		prepareData();
		
		run.state.code = "IP-V";
		run.state.user = "test";

		//make complete valuation
        run.valuation.valid = TBoolean.TRUE;
        for (Lane l : run.lanes) {
        	l.valuation.valid = TBoolean.TRUE;
        }
                
        run.lanes.get(0).valuation.valid = TBoolean.FALSE; // Valuation non completed
        
		r1 = callAction(controllers.runs.api.routes.ref.State.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(run.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
		assertThat(r.state.code).isEqualTo("IP-V");
	}
	

	@Test
	public void testRunStateIWRG() {
		prepareData();
		
		run.state.code = "IW-RG";
		run.state.user = "test";

		//make complete valuation
        run.valuation.valid = TBoolean.TRUE;
        for (Lane l : run.lanes) {
        	l.valuation.valid = TBoolean.TRUE;
        }
                
        run.lanes.get(0).valuation.valid = TBoolean.FALSE; // Valuation non completed
        
		r1 = callAction(controllers.runs.api.routes.ref.State.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(run.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
		assertThat(r.state.code).isEqualTo("IW-RG");	
	}

	//a vérifier
	@Test
	public void testRunStateIWV() {
		prepareData();

		run.state.code = "IW-V";
		run.state.user = "test";

		//make complete valuation
        run.valuation.valid = TBoolean.TRUE;
 
        Assert.assertTrue(Workflows.atLeastOneValuation(run) == true);
        
		r1 = callAction(controllers.runs.api.routes.ref.State.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonState(run.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		Run r = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
		//TODO : A Vérifier  
		assertThat(r.state.code).isEqualTo("IW-V");	
	}
		
	
	@Test
	public void testReadSetStateN() {
		prepareData();

		assertThat(run.state.code).isEqualTo("N");
		assertThat(readset.state.code).isEqualTo("N");
	}
        

	@Test
	public void testReadSetStateIWQC() {
		prepareData();
		
		readset.state.code = "IW-QC";
		readset.state.user = "test";
		
		readset.bioinformaticValuation.valid = TBoolean.FALSE;

		r1 = callAction(controllers.readsets.api.routes.ref.ReadSets.state(readset.code), fakeRequest().withJsonBody(RunMockHelper.getJsonState(readset.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		ReadSet rd = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code",readset.code));
		assertThat(rd.state.code).isEqualTo("IW-QC");
	}
	
	@Test
	public void testReadSetStateIWVQC() {
		prepareData();
		
		readset.state.code = "IW-VQC";
		readset.state.user = "test";
		
		readset.productionValuation.valid = TBoolean.FALSE;

		r1 = callAction(controllers.readsets.api.routes.ref.ReadSets.state(readset.code), fakeRequest().withJsonBody(RunMockHelper.getJsonState(readset.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		ReadSet rd = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code",readset.code));
		assertThat(rd.state.code).isEqualTo("A");
	}


	@Test
	public void testReadSetStateIPQC() {
		prepareData();
		
		readset.state.code = "IP-QC";
		readset.state.user = "test";

		readset.bioinformaticValuation.valid = TBoolean.FALSE;

		r1 = callAction(controllers.readsets.api.routes.ref.ReadSets.state(readset.code), fakeRequest().withJsonBody(RunMockHelper.getJsonState(readset.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		ReadSet rd = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code",readset.code));
		assertThat(rd.state.code).isEqualTo("IP-QC");
	}
	

	@Test
	public void testReadSetStateIPVQC() {
		prepareData();
		
		readset.state.code = "IP-VQC";
		readset.state.user = "test";

		readset.productionValuation.valid = TBoolean.FALSE;
		
		r1 = callAction(controllers.readsets.api.routes.ref.ReadSets.state(readset.code), fakeRequest().withJsonBody(RunMockHelper.getJsonState(readset.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		ReadSet rd = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code",readset.code));
		assertThat(rd.state.code).isEqualTo("A");
	}
	

	@Test
	public void testReadSetStateIWBA() {
		prepareData();
		
		readset.state.code = "IW-BA";
		readset.state.user = "test";
		
		readset.bioinformaticValuation.valid = TBoolean.UNSET;
		readset.productionValuation.valid = TBoolean.UNSET;

		r1 = callAction(controllers.readsets.api.routes.ref.ReadSets.state(readset.code), fakeRequest().withJsonBody(RunMockHelper.getJsonState(readset.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		ReadSet rd = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code",readset.code));
		assertThat(rd.state.code).isEqualTo("IW-BA");
	}

	
	@Test
	public void testReadSetStateIWVBA() {
		prepareData();
		
		readset.state.code = "IW-VBA";
		readset.state.user = "test";
		
		readset.bioinformaticValuation.valid = TBoolean.UNSET;
		readset.productionValuation.valid = TBoolean.UNSET;

		r1 = callAction(controllers.readsets.api.routes.ref.ReadSets.state(readset.code), fakeRequest().withJsonBody(RunMockHelper.getJsonState(readset.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		ReadSet rd = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code",readset.code));
		assertThat(rd.state.code).isEqualTo("A");
	}


	@Test
	public void testReadSetStateFVBA() {
		prepareData();

		readset.state.code = "F-VBA";
		readset.state.user = "test";

		readset.bioinformaticValuation.valid = TBoolean.FALSE;
		readset.productionValuation.valid = TBoolean.FALSE;

		r1 = callAction(controllers.readsets.api.routes.ref.ReadSets.state(readset.code), fakeRequest().withJsonBody(RunMockHelper.getJsonState(readset.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		ReadSet rd = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code",readset.code));
		assertThat(rd.state.code).isEqualTo("A");
	}

		
	@Test
	public void testReadSetStateA() {
		prepareData();

		readset.state.code = "A";
		readset.state.user = "test";

		readset.bioinformaticValuation.valid = TBoolean.TRUE;
		readset.productionValuation.valid = TBoolean.TRUE;

		r1 = callAction(controllers.readsets.api.routes.ref.ReadSets.state(readset.code), fakeRequest().withJsonBody(RunMockHelper.getJsonState(readset.state)));
		assertThat(status(r1)).isEqualTo(OK);
		
		ReadSet rd = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code",readset.code));
		assertThat(rd.state.code).isEqualTo("A");
	}
}
