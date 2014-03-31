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
	
	@BeforeClass
	public static void initData() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		   Container c = new Container();
		   c.code ="containerTest1";
		   c.support = new LocationOnContainerSupport(); 
		   c.support.supportCode = "containerName"; 
		   
		   MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME, c);
	}
	
	
	@AfterClass
	public static void deleteData(){
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class).toList();
		for (Sample sample : samples) {
			MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, sample);
		}
		List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList();
		for (Container container : containers) {
			MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME, container);
		}
	}
	

	@Test
	public void testRunStates() { 
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
		run.state.code = "N";
		Lane lane = RunMockHelper.newLane(1);
		List<Lane> lanes = new ArrayList<Lane>();
		lanes.add(lane);
		run.lanes = lanes;
        

		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		assertThat(status(result)).isEqualTo(OK);
		
		run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
        
		assertThat(run.state.code).isEqualTo("N"); //disponible

		ReadSet readSet = RunMockHelper.newReadSet("ReadSet00");		
		readSet.runCode = run.code;
		readSet.laneNumber = lane.number;
        
		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readSet)));
        assertThat(status(result)).isEqualTo(OK);
        
        
        readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code",readSet.code));
        
		assertThat(readSet.state.code).isEqualTo("N"); 

		
		 State nextState = new State();
		 nextState.code = "IP-S";
		 nextState.date = new Date();
		 nextState.user = "testeur";

		 Workflows.setRunState(new ContextValidation(), run, nextState);
			
	     assertThat(run.state.code).isEqualTo("IP-S"); //
	     
		 
	     nextState = new State();
		 nextState.code = "FE-S";
		 nextState.date = new Date();
		 nextState.user = "testeur";
		
		 Workflows.setRunState(new ContextValidation(), run, nextState);
			
	     assertThat(run.state.code).isEqualTo("FE-S"); //

	     
		 nextState = new State();
		 nextState.code = "F-S";
		 nextState.date = new Date();
		 nextState.user = "testeur";
		 
		 Workflows.setRunState(new ContextValidation(), run, nextState);
			
	     assertThat(run.state.code).isEqualTo("IW-RG"); //
	     
	     
		 nextState = new State();
		 nextState.code = "IP-RG";
		 nextState.date = new Date();
		 nextState.user = "testeur";
		 
		 Workflows.setRunState(new ContextValidation(), run, nextState);
			
	     assertThat(run.state.code).isEqualTo("IP-RG"); //
	     
	     
		 nextState = new State();
		 nextState.code = "F-RG";
		 nextState.date = new Date();
		 nextState.user = "testeur";
		 
		 Workflows.setRunState(new ContextValidation(), run, nextState);
			
	     assertThat(run.state.code).isEqualTo("IW-V"); //why ?
	     
	     
		 nextState = new State();
		 nextState.code = "IP-V";
		 nextState.date = new Date();
		 nextState.user = "testeur";
		 
		 Workflows.setRunState(new ContextValidation(), run, nextState);
		
         assertThat(run.state.code).isEqualTo("IP-V"); //
        
         //make complete valuation
         run.valuation.valid = TBoolean.TRUE;
         for (Lane l : run.lanes) {
        	l.valuation.valid = TBoolean.TRUE;
         }
        
         Logger.debug("************** Valuation completed ***************");
        
        
         Workflows.nextRunState(new ContextValidation(), run);
        
         assertThat(run.state.code).isEqualTo("F-V");
        
        
         Workflows.nextRunState(new ContextValidation(), run);
        
         assertThat(run.state.code).isEqualTo("F-V");
        
         run.lanes.get(0).valuation.valid = TBoolean.FALSE;
        
         Logger.debug("************** Valuation non completed ***************");
        
         assertThat(Workflows.isRunValuationComplete(run)).isEqualTo(true);
         assertThat(Workflows.atLeastOneValuation(run)).isEqualTo(true);
        
        
         Workflows.nextRunState(new ContextValidation(), run);
        
         assertThat(run.state.code).isEqualTo("F-V");
        
         Logger.debug("************** set lane to UNSET  **************");
        
         run.lanes.get(0).valuation.valid = TBoolean.UNSET;
        
         assertThat(Workflows.isRunValuationComplete(run)).isEqualTo(false);
        
        
         Workflows.nextRunState(new ContextValidation(), run);
        
         assertThat(run.state.code).isEqualTo("IP-V");
        
         Logger.debug("Lane 0 valuation status : " + run.lanes.get(0).valuation.valid);
  
         assertThat(Workflows.isRunValuationComplete(run)).isEqualTo(false);
         Workflows.nextRunState(new ContextValidation(), run);        
         
         assertThat(run.state.code).isEqualTo("IP-V");
        
        
        /********************* tests readSet states ***************************************************/ 

        assertThat(readSet.state.code).isEqualTo("N");

        
		 nextState = new State();
		 nextState.code = "IW-RG";
		 nextState.date = new Date();
		 nextState.user = "testeur";
		 
		 Workflows.setRunState(new ContextValidation(), run, nextState);
			
	     assertThat(run.state.code).isEqualTo("IW-RG");
	     
	     
		 nextState = new State();
		 nextState.code = "IP-RG";
		 nextState.date = new Date();
		 nextState.user = "testeur";
		 
		 Workflows.setRunState(new ContextValidation(), run, nextState);
			
	     assertThat(run.state.code).isEqualTo("IP-RG");
	     
	     
		 nextState = new State();
		 nextState.code = "F-RG";
		 nextState.date = new Date();
		 nextState.user = "testeur";
		 
		 Workflows.setRunState(new ContextValidation(), run, nextState);
				
	     assertThat(run.state.code).isEqualTo("IP-V");
	     
		 readSet.bioinformaticValuation.valid = TBoolean.FALSE;

		 
		 Workflows.setReadSetState(new ContextValidation(), readSet, nextState);
	        
	     assertThat(readSet.state.code).isEqualTo("IW-QC");
	     
	     
		 nextState = new State();
		 nextState.code = "IP-QC";
		 nextState.date = new Date();
		 nextState.user = "testeur";
		 
		 Workflows.setReadSetState(new ContextValidation(), readSet, nextState);
	     
	     assertThat(readSet.state.code).isEqualTo("IP-QC");
	     
	
		 nextState = new State();
		 nextState.code = "F-QC";
		 nextState.date = new Date();
		 nextState.user = "testeur";
		 readSet.bioinformaticValuation.valid = TBoolean.UNSET;
		 readSet.productionValuation.valid = TBoolean.UNSET;
		 
		 Workflows.setReadSetState(new ContextValidation(), readSet, nextState);
	     
	     assertThat(readSet.state.code).isEqualTo("IW-V");
	
	 
	     readSet.bioinformaticValuation.valid = TBoolean.UNSET;
    	     
		 Workflows.setReadSetState(new ContextValidation(), readSet, nextState);
	        
		 
	     assertThat(readSet.state.code).isEqualTo("IW-V");
	     
	     
	     readSet.bioinformaticValuation.valid = TBoolean.FALSE;
	     readSet.productionValuation.valid = TBoolean.FALSE;

		 Workflows.setReadSetState(new ContextValidation(), readSet, nextState);
	     		
	     assertThat(readSet.state.code).isEqualTo("UA");
	     
	     
	     readSet.bioinformaticValuation.valid = TBoolean.TRUE;
	     readSet.productionValuation.valid = TBoolean.TRUE;

		 Workflows.setReadSetState(new ContextValidation(), readSet, nextState);
	     
		
	     assertThat(readSet.state.code).isEqualTo("A");

	     }
}
