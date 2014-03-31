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
	


	/*
	//COMMON
	l.add(newState("Disponible", "A", true, 1000, StateCategory.find.findByCode("N"), getObjectTypes(ObjectType.CODE.Container.name(), ObjectType.CODE.ReadSet.name()) ));
	l.add(newState("Indisponible", "UA", true, 1000, StateCategory.find.findByCode("N"), getObjectTypes(ObjectType.CODE.Container.name(), ObjectType.CODE.ReadSet.name()) ));
	l.add(newState("Terminé", "F", true, 1000, StateCategory.find.findByCode("F"), getObjectTypes(ObjectType.CODE.Project.name(), ObjectType.CODE.Experiment.name(), ObjectType.CODE.Process.name(), ObjectType.CODE.Run.name(), ObjectType.CODE.Sample.name(), ObjectType.CODE.Instrument.name(), ObjectType.CODE.Reagent.name(), ObjectType.CODE.Import.name(), ObjectType.CODE.Treatment.name()) ));
	l.add(newState("Terminé en erreur", "FE", true, 1000, StateCategory.find.findByCode("F"), null));
	l.add(newState("Terminé en ", "FS", true, 1000, StateCategory.find.findByCode("F"), null));
	l.add(newState("Contrôle qualité en attente", "IW-QC", true, 401, StateCategory.find.findByCode("IW"), getObjectTypes(ObjectType.CODE.Container.name(), ObjectType.CODE.ReadSet.name()) ));	
	l.add(newState("Contrôle qualité en cours", "IP-QC", true, 450, StateCategory.find.findByCode("IP"), getObjectTypes(ObjectType.CODE.Container.name(), ObjectType.CODE.ReadSet.name()) ));	
	l.add(newState("Contrôle qualité terminé", "F-QC", true, 500, StateCategory.find.findByCode("F"), getObjectTypes(ObjectType.CODE.Container.name(), ObjectType.CODE.ReadSet.name()) ));	
	l.add(newState("Evaluation en attente", "IW-V", true, 601, StateCategory.find.findByCode("IW"), getObjectTypes(ObjectType.CODE.Container.name(), ObjectType.CODE.Run.name(), ObjectType.CODE.ReadSet.name()) ));
	l.add(newState("Evaluation en cours", "IP-V", true, 651, StateCategory.find.findByCode("IP"), getObjectTypes(ObjectType.CODE.Container.name(), ObjectType.CODE.Run.name()ObjectType.CODE.ReadSet.name()) ));		
	//NGL-SQ
	l.add(newState("Nouveau", "N", true, 0, StateCategory.find.findByCode("N"), getObjectTypes(ObjectType.CODE.Project.name(), ObjectType.CODE.Experiment.name(), ObjectType.CODE.Proce, ObjectType.CODE.ReadSet.name()) ));
	l.add(newState("Evaluation terminée", "F-V", true, 701, StateCategory.find.findByCode("F"), getObjectTypes(ObjectType.CODE.Container.name(), ObjectType.CODE.Run.name(), ss.name(), ObjectType.CODE.Run.name(), ObjectType.CODE.ReadSet.name(), ObjectType.CODE.Sample.name(), ObjectType.CODE.Instrument.name(), ObjectType.CODE.Reagent.name(), ObjectType.CODE.Import.name(), ObjectType.CODE.Treatment.name()) ));
	l.add(newState("En cours", "IP", true, 500, StateCategory.find.findByCode("IP"), getObjectTypes(ObjectType.CODE.Project.name(), ObjectType.CODE.Experiment.name(), ObjectType.CODE.Process.name(), ObjectType.CODE.Sample.name(), ObjectType.CODE.Instrument.name(), ObjectType.CODE.Reagent.name(), ObjectType.CODE.Import.name(), ObjectType.CODE.Treatment.name()) ));
	l.add(newState("Processus en attente", "IW-P", true, 101, StateCategory.find.findByCode("IW"), getObjectTypes(ObjectType.CODE.Container.name())));
	l.add(newState("Expérience en attente", "IW-E", true, 201, StateCategory.find.findByCode("IW"), getObjectTypes(ObjectType.CODE.Container.name())));
	l.add(newState("En cours d'utilisation", "IU", true, 250, StateCategory.find.findByCode("IP"), getObjectTypes(ObjectType.CODE.Container.name())));
	l.add(newState("En stock", "IS", true, 900, StateCategory.find.findByCode("N"), getObjectTypes(ObjectType.CODE.Container.name())));
	//NGL-BI
	l.add(newState("Séquençage en cours", "IP-S", true, 150, StateCategory.find.findByCode("IP"), getObjectTypes(ObjectType.CODE.Run.name())));
	l.add(newState("Séquençage en echec", "FE-S", true, 199, StateCategory.find.findByCode("F"), getObjectTypes(ObjectType.CODE.Run.name())));
	l.add(newState("Séquençage terminé", "F-S", true, 200, StateCategory.find.findByCode("F"), getObjectTypes(ObjectType.CODE.Run.name())));	
	l.add(newState("Read generation en attente", "IW-RG", true, 201, StateCategory.find.findByCode("IW"), getObjectTypes(ObjectType.CODE.Run.name())));
	l.add(newState("Read generation en cours", "IP-RG", true, 250, StateCategory.find.findByCode("IP"), getObjectTypes(ObjectType.CODE.Run.name(), ObjectType.CODE.ReadSet.name())));
	l.add(newState("Read generation terminée", "F-RG", true, 300, StateCategory.find.findByCode("F"), getObjectTypes(ObjectType.CODE.Run.name(), ObjectType.CODE.ReadSet.name()) ));
	*/
	
	
    /*
	run.state.code =   "IP-V"; // evaluation en cours
	run.state.code =   "F-V"; // evaluation termine
	run.state.code =  "IP-S"; //Sequençage en cours
	run.state.code =  "FE-S"; //Sequençage en echec
	run.state.code =  "F-S"; //Sequençage termine	
	run.state.code =  "IW-RG"; //Read generation en attente
	run.state.code =  "IP-RG"; //Read generation en cours
	run.state.code =  "F-RG"; //Read generation terminee
	*/
	
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
        
		Logger.debug("************** run.state.code : N**************");
		assertThat(run.state.code).isEqualTo("N"); //ok
		
		ContextValidation ctx = new ContextValidation(); 
		 State nextState = new State();
		 nextState.code = "IP-V";
		 nextState.date = new Date();
		 nextState.user = "testeur";
		Workflows.setRunState(ctx, run, nextState);
		
        //result = callAction(controllers.runs.api.routes.ref.State.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
        //result = callAction(controllers.runs.api.routes.ref.Runs.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
        run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
        
        
        Logger.debug("************** run.state.code : IP-V**************");
        assertThat(run.state.code).isEqualTo("IP-V");
        
        //make complete valuation
        run.valuation.valid = TBoolean.TRUE;
        for (Lane l : run.lanes) {
        	l.valuation.valid = TBoolean.TRUE;
        }
        
        result = callAction(controllers.runs.api.routes.ref.Runs.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
        assertThat(status(result)).isEqualTo(200);
                
        Logger.debug("************** Valuation completed ***************");
        
        ctx = new ContextValidation();
        Workflows.nextRunState(ctx, run);
        
       run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
        
        Logger.debug("************** run.state.code : F-V**************");
        assertThat(run.state.code).isEqualTo("F-V");
        
        ctx = new ContextValidation();
        Workflows.nextRunState(ctx, run);
        
       run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
        
        Logger.debug("************** run.state.code : F-V**************");
        assertThat(run.state.code).isEqualTo("F-V");
        
        run.lanes.get(0).valuation.valid = TBoolean.FALSE;
        Logger.debug("************** Valuation non completed ***************");
        
        assertThat(Workflows.isRunValuationComplete(run)).isEqualTo(true);
        assertThat(Workflows.atLeastOneValuation(run)).isEqualTo(true);
        
        Workflows.nextRunState(ctx, run);
        
       run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
        
        Logger.debug("************** run.state.code : F-V**************");
        assertThat(run.state.code).isEqualTo("F-V");
        
        Logger.debug("************** set lane to UNSET  **************");
        run.lanes.get(0).valuation.valid = TBoolean.UNSET;
        
        assertThat(Workflows.isRunValuationComplete(run)).isEqualTo(false);
        
        
        Workflows.nextRunState(ctx, run);
        
       run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
        
        Logger.debug("************** run.state.code : IP-V**************");
        assertThat(run.state.code).isEqualTo("IP-V");
        
        Logger.debug("Lane 0 valuation status : " + run.lanes.get(0).valuation.valid); // Lane 0 valuation status : TRUE !!!!!!!!
  
        //assertThat(Workflows.isRunValuationComplete(run)).isEqualTo(false);
        
        Workflows.nextRunState(ctx, run);
        
        Logger.debug("************** run.state.code : IP-V**************");
        //assertThat(run.state.code).isEqualTo("IP-V");
        
        
        
        
    }


}
