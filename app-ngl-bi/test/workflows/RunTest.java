package workflows;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.State;
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
import net.vz.mongodb.jackson.DBQuery;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Logger;
import play.mvc.Result;
import fr.cea.ig.MongoDBDAO;
import utils.AbstractTests;
import utils.RunMockHelper;
import validation.ContextValidation;


public class RunTest extends  AbstractTests {	
	
	static Container c;
	Run run;
	ReadSet readset;
	Sample sample;
	Project project;
	Result r1;
	Result r2;
	
	
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
	
	
	public void setRunStateTo(String stateCode) {
		 State nextState = new State();
		 nextState.code = stateCode;
		 nextState.date = new Date();
		 nextState.user = "testeur";
		 Workflows.setRunState(new ContextValidation(), run, nextState);
	}
	

	
	public void setReadSetStateTo(String stateCode) {
		 State nextState = new State();
		 nextState.code = stateCode;
		 nextState.date = new Date();
		 nextState.user = "testeur";
		 Workflows.setReadSetState(new ContextValidation(), readset, nextState);
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
		 setRunStateTo("IP-S");
		 assertThat(run.state.code).isEqualTo("IP-S"); 
	}  


	@Test
	public void testRunStateFES() {
		prepareData();
		setRunStateTo("IP-S");
		setRunStateTo("FE-S");
	    assertThat(run.state.code).isEqualTo("FE-S");
	}
	

	@Test
	public void testRunStateFS() {
		prepareData();		
		setRunStateTo("IP-S");
		setRunStateTo("FE-S");
		setRunStateTo("F-S");
	    assertThat(run.state.code).isEqualTo("IW-RG");
	}
	
	
	@Test
	public void testRunStateIPRG() {
		prepareData();	     
		setRunStateTo("IP-S");
		setRunStateTo("FE-S");
		setRunStateTo("F-S");
		setRunStateTo("IP-RG");
		assertThat(run.state.code).isEqualTo("IP-RG");
	}
	
	
	
	@Test
	public void testRunStateFRG() {
		prepareData();	
		setRunStateTo("IP-S");
		setRunStateTo("FE-S");
		setRunStateTo("F-S");
		setRunStateTo("IP-RG");
		setRunStateTo("F-RG");
		assertThat(run.state.code).isEqualTo("IW-V");
	}
	     
	 
	@Test
	public void testRunStateIPV() {
		prepareData();
		setRunStateTo("IP-S");
		setRunStateTo("FE-S");
		setRunStateTo("F-S");
		setRunStateTo("IP-RG");
		setRunStateTo("F-RG");
		setRunStateTo("IP-V");
		assertThat(run.state.code).isEqualTo("IP-V");
	}

	
	@Test
	public void testRunStateFV() {
		prepareData();
		setRunStateTo("IP-S");	
		setRunStateTo("FE-S");
		setRunStateTo("F-S");
		setRunStateTo("IP-RG");
		setRunStateTo("F-RG");
		setRunStateTo("IP-V");
        //make complete valuation
        run.valuation.valid = TBoolean.TRUE;
        for (Lane l : run.lanes) {
       	l.valuation.valid = TBoolean.TRUE;
        }
        Workflows.nextRunState(new ContextValidation(), run);
        
         assertThat(run.state.code).isEqualTo("F-V");
	}
	
	
	@Test
	public void testRunStateFV2() {
		prepareData();	
		setRunStateTo("IP-S");	
		setRunStateTo("FE-S");
		setRunStateTo("F-S");
		setRunStateTo("IP-RG");
		setRunStateTo("F-RG");
		setRunStateTo("IP-V");
        //make complete valuation
        run.valuation.valid = TBoolean.TRUE;
        for (Lane l : run.lanes) {
       	l.valuation.valid = TBoolean.TRUE;
        }
        Workflows.nextRunState(new ContextValidation(), run);
        Workflows.nextRunState(new ContextValidation(), run);
        
        assertThat(run.state.code).isEqualTo("F-V");
	}
	
	
	
	@Test
	public void testRunStateBackToFV() {
		prepareData();	
		setRunStateTo("IP-S");		
		setRunStateTo("FE-S");		
		setRunStateTo("F-S");
		setRunStateTo("IP-RG");	     
		setRunStateTo("F-RG");
		setRunStateTo("IP-V");
        //make complete valuation
        run.valuation.valid = TBoolean.TRUE;
        for (Lane l : run.lanes) {
        	l.valuation.valid = TBoolean.TRUE;
        }
        Workflows.nextRunState(new ContextValidation(), run);
                
        run.lanes.get(0).valuation.valid = TBoolean.FALSE; // Valuation non completed
        
         Workflows.nextRunState(new ContextValidation(), run);
        
         assertThat(run.state.code).isEqualTo("F-V");    
	}
	
	
	
	
	@Test
	public void testRunStateBackToIPV() {
		prepareData();	
		setRunStateTo("IP-S");
		setRunStateTo("FE-S");
		setRunStateTo("F-S");
		setRunStateTo("IP-RG");
		setRunStateTo("F-RG");
		setRunStateTo("IP-V");
        //make complete valuation
        run.valuation.valid = TBoolean.TRUE;
        for (Lane l : run.lanes) {
        	l.valuation.valid = TBoolean.TRUE;
        }
        
        Workflows.nextRunState(new ContextValidation(), run);
                
        run.lanes.get(0).valuation.valid = TBoolean.FALSE; // Valuation non completed
        
         Workflows.nextRunState(new ContextValidation(), run);
        
         run.lanes.get(0).valuation.valid = TBoolean.UNSET;
        
         Workflows.nextRunState(new ContextValidation(), run);
        
         assertThat(run.state.code).isEqualTo("IP-V");
	}
	
	

	@Test
	public void testRunStateIPV_IPV() {
		prepareData();	
		setRunStateTo("IP-S");
		setRunStateTo("FE-S");	
		setRunStateTo("F-S");
		setRunStateTo("IP-RG");
		setRunStateTo("F-RG");
		setRunStateTo("IP-V");	
        //make complete valuation
        run.valuation.valid = TBoolean.TRUE;
        for (Lane l : run.lanes) {
        	l.valuation.valid = TBoolean.TRUE;
        }
        
        Workflows.nextRunState(new ContextValidation(), run);
                
        run.lanes.get(0).valuation.valid = TBoolean.FALSE; // Valuation non completed
        
         Workflows.nextRunState(new ContextValidation(), run);	
        
         run.lanes.get(0).valuation.valid = TBoolean.UNSET;
        
         Workflows.nextRunState(new ContextValidation(), run);
        
         //assertThat(run.lanes.get(0).valuation.valid).isEqualTo(TBoolean.UNSET);
         
         Workflows.nextRunState(new ContextValidation(), run);        
         
         assertThat(run.state.code).isEqualTo("IP-V");  
	}

        
	
	@Test
	public void testRunStateIWRG() {
		prepareData();        
		setRunStateTo("IW-RG");		
	    assertThat(run.state.code).isEqualTo("IW-RG");
	}
	     

	
	@Test
	public void testRunStateIPRG2() {
		prepareData();
		setRunStateTo("IW-RG");
		setRunStateTo("IP-RG");
	    assertThat(run.state.code).isEqualTo("IP-RG");
	}


	
	@Test
	public void testRunStateIWVorIPV() {
		prepareData();
		setRunStateTo("IW-RG");
		setRunStateTo("IP-RG");
		setRunStateTo("F-RG");

		boolean bUnset = true;
		for (Lane l : run.lanes) {
			if (l.valuation.valid != TBoolean.UNSET) bUnset = false;
		}
		
		if (run.valuation.valid == TBoolean.UNSET && bUnset) {
			assertThat(run.state.code).isEqualTo("IW-V");
		}
		else {
			assertThat(run.state.code).isEqualTo("IP-V");
		}
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
		
		readset.bioinformaticValuation.valid = TBoolean.FALSE;

		setReadSetStateTo("IW-QC");
	        
	    assertThat(readset.state.code).isEqualTo("IW-QC");
	}


	
	@Test
	public void testReadSetStateIPQC() {
		prepareData();
		
		readset.bioinformaticValuation.valid = TBoolean.FALSE;

		setReadSetStateTo("IW-QC");
		setReadSetStateTo("IP-QC");
		 
	    assertThat(readset.state.code).isEqualTo("IP-QC");
	}
	

	
	
	@Test
	public void testReadSetStateIWV() {
		prepareData();
		
		 readset.bioinformaticValuation.valid = TBoolean.FALSE;

		 setReadSetStateTo("IW-QC");
		 setReadSetStateTo("IP-QC");

		 readset.bioinformaticValuation.valid = TBoolean.UNSET;
		 readset.productionValuation.valid = TBoolean.UNSET;

		 setReadSetStateTo("F-QC");
		 
	     assertThat(readset.state.code).isEqualTo("F-QC"); //instead of IW-V
	}

	


	@Test
	public void testReadSetStateUA() {
		prepareData();
		
		 readset.bioinformaticValuation.valid = TBoolean.FALSE;

		 setReadSetStateTo("IW-QC");
		 setReadSetStateTo("IP-QC");

		 readset.bioinformaticValuation.valid = TBoolean.FALSE;
		 readset.productionValuation.valid = TBoolean.FALSE;

		 setReadSetStateTo("F-QC");

	     assertThat(readset.state.code).isEqualTo("F-QC"); //instead of UA
	}

	
	     

	@Test
	public void testReadSetStateA() {
		prepareData();
		
		 readset.bioinformaticValuation.valid = TBoolean.FALSE;

		 setReadSetStateTo("IW-QC");
		 setReadSetStateTo("IP-QC");

		 readset.bioinformaticValuation.valid = TBoolean.TRUE;
		 readset.productionValuation.valid = TBoolean.TRUE;

		 setReadSetStateTo("F-QC");	     
		
	     assertThat(readset.state.code).isEqualTo("F-QC"); //instead of A
	}
}
