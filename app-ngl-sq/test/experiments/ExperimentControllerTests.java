package experiments;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongojack.DBQuery;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import utils.AbstractTests;
import utils.InitDataHelper;
import fr.cea.ig.MongoDBDAO;

public class ExperimentControllerTests extends AbstractTests {
	
	@BeforeClass
	public static void initData() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		InitDataHelper.initForProcessesTest();
	}

	@AfterClass	
	public static  void resetData(){
		InitDataHelper.endTest();
	}
	
	
	@Test
	public void saveManyToOneExperiment() throws JsonParseException, JsonMappingException, IOException{
		
		List<Experiment> exps = MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME+"_new", Experiment.class,DBQuery.is("typeCode", "prepa-flowcell")).toList();
		Experiment exp=exps.get(0);
		exp._id=null;
		Result result = callAction(controllers.experiments.api.routes.ref.Experiments.save(),fakeRequest().withJsonBody(Json.toJson(exp)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);

		ObjectMapper mapper=new ObjectMapper();

		exp=mapper.readValue(play.test.Helpers.contentAsString(result),Experiment.class);
		assertThat(exp.state.code).isEqualTo("N");
		assertThat(exp.projectCodes).isNotNull();
		assertThat(exp.sampleCodes).isNotNull();
		assertThat(exp.inputContainerSupportCodes).isNotNull();
		assertThat(exp.outputContainerSupportCodes).isNull();
		//Valide process = "IP", InputContainer ="IW-E"
		List<ContainerUsed> containersUsed=exp.getAllInputContainers();
		for(ContainerUsed containerUsed:containersUsed){
			Container container=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerUsed.code);
			assertThat(container.state.code).isEqualTo("IW-E");
			List<Process> processes=MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.in("code", container.inputProcessCodes)).toList();
			for(Process process:processes){
				assertThat(process.state.code).isEqualTo("N");
			}
		}
		
		
	}
	
	
	@Test
	public void updateExperimentProperties(){
		Experiment exp = ExperimentTestHelper.getFakePrepFlowcell();
		Logger.debug("Experiment "+exp.instrument.outContainerSupportCategoryCode);
		MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
		exp.experimentProperties=new HashMap<String, PropertyValue>();
		exp.experimentProperties.put("TEST", new PropertySingleValue("VALUE"));
		Result result = callAction(controllers.experiments.api.routes.ref.Experiments.updateExperimentProperties(exp.code),fakeRequest().withJsonBody(Json.toJson(exp)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.BAD_REQUEST);
		Experiment expsave=MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, exp.code);
		assertThat(expsave.experimentProperties).isEmpty();
		Logger.debug("Experiment "+exp.code);
		MongoDBDAO.deleteByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class,exp.code);
	}

	
	@Test
	public void updateExperimentInformations(){
		Experiment exp = ExperimentTestHelper.getFakePrepFlowcell();
		MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
		exp.protocolCode="Protocol";
		exp.traceInformation=new TraceInformation();
		Result result = callAction(controllers.experiments.api.routes.ref.Experiments.updateExperimentInformations(exp.code),fakeRequest().withJsonBody(Json.toJson(exp)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		Experiment expSave=MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, exp.code);
		assertThat(expSave.protocolCode).isEqualTo(exp.protocolCode);
		
		MongoDBDAO.deleteByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class,exp.code);
	}
	
	
	
	@Test
	public void updateInstrumentInformations(){
		Experiment exp = new Experiment(); 
		Random randomGenerator=new Random();
		exp.code = "TEST"+randomGenerator.nextInt(1000);
		MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
		
		exp.instrument=new InstrumentUsed();
        exp.instrument.code= "APOLLON";
        exp.instrument.categoryCode= "opt-map-opgen";
        exp.instrument.inContainerSupportCategoryCode= "tube";
        exp.instrument.outContainerSupportCategoryCode= "mapcard";
        exp.instrument.typeCode= "ARGUS";
    
		Result result = callAction(controllers.experiments.api.routes.ref.Experiments.updateInstrumentInformations(exp.code),fakeRequest().withJsonBody(Json.toJson(exp)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		Experiment expSave=MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, exp.code);
		MongoDBDAO.deleteByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class,exp.code);

		assertThat(expSave.instrument).isNotNull();
		assertThat(expSave.instrument.code).isEqualTo(exp.instrument.code);
		assertThat(expSave.instrument.typeCode).isEqualTo(exp.instrument.typeCode);
		assertThat(expSave.instrument.categoryCode).isEqualTo(exp.instrument.categoryCode);
		assertThat(expSave.instrument.inContainerSupportCategoryCode).isEqualTo(exp.instrument.inContainerSupportCategoryCode);
		assertThat(expSave.instrument.outContainerSupportCategoryCode).isEqualTo(exp.instrument.outContainerSupportCategoryCode);
		
	}

	
	@Test
	public void updateInstrumentProperties(){
		Experiment exp = ExperimentTestHelper.getFakePrepFlowcell();		
		MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);

		exp.instrumentProperties=new HashMap<String, PropertyValue>();
		exp.instrumentProperties.put("controlLane", new PropertySingleValue("1"));
		exp.instrumentProperties.put("sequencingProgramType", new PropertySingleValue("SR"));
		exp.instrumentProperties.put("containerSupportCode", new PropertySingleValue("TEST1"));
		Result result = callAction(controllers.experiments.api.routes.ref.Experiments.updateInstrumentProperties(exp.code),fakeRequest().withJsonBody(Json.toJson(exp)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		Experiment expSave=MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, exp.code);
		MongoDBDAO.deleteByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class,exp.code);
		assertThat(expSave.instrumentProperties.size()).isGreaterThan(0).isEqualTo(3);
		assertThat(expSave.instrumentProperties.get("controlLane").value.toString()).isEqualTo("1");

	}

	
		@Test
		public void updateContainers(){
			Experiment exp = ExperimentTestHelper.getFakePrepFlowcell();
			MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
			exp.typeCode="illumina-depot";
			Experiment expFake=ExperimentTestHelper.getFakeExperimentWithAtomicExperimentManyToOne("illumina-depot");
			exp.atomicTransfertMethods=expFake.atomicTransfertMethods;
			
			Result result = callAction(controllers.experiments.api.routes.ref.Experiments.updateContainers(exp.code),fakeRequest().withJsonBody(Json.toJson(exp)));
			assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
			
			Experiment expSave=MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, exp.code);
			MongoDBDAO.deleteByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class,exp.code);
			assertThat(expSave.atomicTransfertMethods).isNotEmpty();
			assertThat(expSave.projectCodes).isNotEmpty();
			assertThat(expSave.sampleCodes).isNotEmpty();
			assertThat(expSave.inputContainerSupportCodes).isNotEmpty();
		}
		
		
	
}
