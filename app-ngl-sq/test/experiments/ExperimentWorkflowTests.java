package experiments;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mongojack.DBQuery;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import utils.AbstractTests;
import utils.InitDataHelper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import controllers.experiments.api.ExperimentUpdateForm;
import fr.cea.ig.MongoDBDAO;

public class ExperimentWorkflowTests extends AbstractTests {
	
	@Before
	public  void initData() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		InitDataHelper.initForProcessesTest();
	}

	@After
	public  void resetData(){
		InitDataHelper.endTest();
	}

	
	@Test
	public void nextStateManyToOne() throws JsonParseException, JsonMappingException, IOException{
		
		String code="PREPA-FLOWCELL-20150107_105554";
		
		// Experiment PREPA-FLOWCELL-20150107_105554 update state code "N" to "IP"
		ExperimentUpdateForm experimentUpdateForm = new ExperimentUpdateForm();
		experimentUpdateForm.nextStateCode = "IP";
		Result result = callAction(controllers.experiments.api.routes.ref.Experiments.updateStateCode(code),fakeRequest().withJsonBody(Json.toJson(experimentUpdateForm)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		Experiment expUpdate=MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, code);
		assertThat(expUpdate.state.code).isEqualTo("IP");
		assertThat(expUpdate.getAllOutPutContainerWhithInPutContainer()).isNotEmpty();
		
		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("support.code", expUpdate.inputContainerSupportCodes) ).toList();
		assertThat(containers).isNotEmpty();
		List<String> processCodes=new ArrayList<String>();
		for(Container container:containers){
			assertThat(container.state.code).isEqualTo("IU");
			InstanceHelpers.addCodesList(container.inputProcessCodes,processCodes);
		}
		List<Process> processes=MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME,Process.class,DBQuery.in("code", processCodes) ).toList();
		for(Process process:processes){
			assertThat(process.state.code).isEqualTo("IP");
			assertThat(code).isIn(process.experimentCodes);
		}
		
		
		// Experiment PREPA-FLOWCELL-20150107_105554 update state "IP" to state "F"
		experimentUpdateForm = new ExperimentUpdateForm();
		experimentUpdateForm.nextStateCode = "F";
		result = callAction(controllers.experiments.api.routes.ref.Experiments.updateStateCode(code),fakeRequest().withJsonBody(Json.toJson(experimentUpdateForm)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		expUpdate=MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, code);
		assertThat(expUpdate.state.code).isEqualTo("F");
		
		assertThat(expUpdate.inputContainerSupportCodes).isNotEmpty();
		assertThat(expUpdate.outputContainerSupportCodes).isNotEmpty();
		
		containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("support.code", expUpdate.inputContainerSupportCodes) ).toList();
		assertThat(containers).isNotEmpty();
		for(Container container:containers){
			assertThat(container.state.code).isEqualTo("IS");
			//assertThat(container.inputProcessCodes).isNull();
		}
		
		processes=MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME,Process.class,DBQuery.in("code", processCodes) ).toList();
		for(Process process:processes){
			assertThat(process.state.code).isEqualTo("IP");
			assertThat(process.currentExperimentTypeCode).isNotNull();
			assertThat(code).isIn(process.experimentCodes);
			assertThat(expUpdate.outputContainerSupportCodes.get(0)).isIn(process.newContainerSupportCodes);
		}
		
		List<Container> outPutContainers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("support.code", expUpdate.outputContainerSupportCodes) ).toList();
		assertThat(outPutContainers).isNotEmpty();
		assertThat(outPutContainers.size()).isEqualTo(expUpdate.atomicTransfertMethods.size());
		for(Container outContainer:outPutContainers){
			assertThat(expUpdate.typeCode).isIn(outContainer.fromExperimentTypeCodes);
			assertThat(outContainer.projectCodes).isNotEmpty();
			assertThat(outContainer.sampleCodes).isNotEmpty();
			assertThat(outContainer.state.code).isEqualTo("A");
			assertThat(outContainer.processTypeCode).isNotEmpty();
			assertThat(outContainer.inputProcessCodes).isNotEmpty();
			for(String processCode:outContainer.inputProcessCodes){
				assertThat(processCode).isIn(processCodes);
			}
		}
		
		//Create depot solexa from container previously created by prepa-flowcell experiment
		List<Experiment> exps = MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME+"_new", Experiment.class,DBQuery.is("typeCode", "illumina-depot")).toList();
		Experiment exp=exps.get(0);
		exp._id=null;
		result = callAction(controllers.experiments.api.routes.ref.Experiments.save(),fakeRequest().withJsonBody(Json.toJson(exp)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		ObjectMapper mapper=new ObjectMapper();

		exp=mapper.readValue(play.test.Helpers.contentAsString(result),Experiment.class);
		assertThat(exp.state.code).isEqualTo("N");
		assertThat(exp.projectCodes).isNotNull();
		assertThat(exp.sampleCodes).isNotNull();
		assertThat(exp.inputContainerSupportCodes.get(0)).isEqualTo(expUpdate.outputContainerSupportCodes.get(0));
		assertThat(exp.outputContainerSupportCodes).isNull();
		
		experimentUpdateForm = new ExperimentUpdateForm();
		experimentUpdateForm.nextStateCode = "IP";
		result = callAction(controllers.experiments.api.routes.ref.Experiments.updateStateCode(exp.code),fakeRequest().withJsonBody(Json.toJson(experimentUpdateForm)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		expUpdate=MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, exp.code);
		assertThat(expUpdate.state.code).isEqualTo("IP");
		assertThat(expUpdate.getAllOutPutContainerWhithInPutContainer()).isEmpty();
		
		processes=MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME,Process.class,DBQuery.in("code", processCodes) ).toList();
		for(Process process:processes){
			assertThat(process.state.code).isEqualTo("IP");
			assertThat(process.currentExperimentTypeCode).isEqualTo(exp.typeCode);
			assertThat(exp.code).isIn(process.experimentCodes);
		}
		
		experimentUpdateForm = new ExperimentUpdateForm();
		experimentUpdateForm.nextStateCode = "F";
		result = callAction(controllers.experiments.api.routes.ref.Experiments.updateStateCode(exp.code),fakeRequest().withJsonBody(Json.toJson(experimentUpdateForm)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		expUpdate=MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, exp.code);
		assertThat(expUpdate.state.code).isEqualTo("F");
		
		processes=MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME,Process.class,DBQuery.in("code", processCodes) ).toList();
		for(Process process:processes){
			Logger.debug("Process Code to Test "+process.code +" with state "+process.state.code);
			assertThat(process.state.code).isEqualTo("IP");
			assertThat(process.currentExperimentTypeCode).isEqualTo(exp.typeCode);
		}
		
		containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("support.code", expUpdate.inputContainerSupportCodes) ).toList();
		assertThat(containers).isNotEmpty();
		for(Container container:containers){
			assertThat(container.state.code).isEqualTo("IS");
		//	assertThat(container.inputProcessCodes).isNull();
		}
		
		
		
	}
	
	//@Test
	public void stopProcess(){
		//resetData();
		try {
			initData();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String code="PREPA-FLOWCELL-20150107_105554";
		
		// Experiment PREPA-FLOWCELL-20150107_105554 update state code "N" to "IP"
		ExperimentUpdateForm experimentUpdateForm = new ExperimentUpdateForm();
		experimentUpdateForm.nextStateCode = "IP";
		Result result = callAction(controllers.experiments.api.routes.ref.Experiments.updateStateCode(code),fakeRequest().withJsonBody(Json.toJson(experimentUpdateForm)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		Experiment expUpdate=MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, code);
		assertThat(expUpdate.state.code).isEqualTo("IP");
		assertThat(expUpdate.getAllOutPutContainerWhithInPutContainer()).isNotEmpty();
		
		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("support.code", expUpdate.inputContainerSupportCodes) ).toList();
		assertThat(containers).isNotEmpty();
		List<String> processCodes=new ArrayList<String>();
		for(Container container:containers){
			assertThat(container.state.code).isEqualTo("IU");
			assertThat(container.processTypeCode).isNotNull();
			InstanceHelpers.addCodesList(container.inputProcessCodes,processCodes);
		}
		List<Process> processes=MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME,Process.class,DBQuery.in("code", processCodes) ).toList();
		for(Process process:processes){
			assertThat(process.state.code).isEqualTo("IP");
			assertThat(code).isIn(process.experimentCodes);
		}
		
		
		// Experiment PREPA-FLOWCELL-20150107_105554 update state "IP" to state "F"
		experimentUpdateForm = new ExperimentUpdateForm();
		experimentUpdateForm.nextStateCode = "F";
		experimentUpdateForm.stopProcess = true;
		experimentUpdateForm.processResolutionCodes=new ArrayList<String>();
		experimentUpdateForm.processResolutionCodes.add("processus-partiel");

		result = callAction(controllers.experiments.api.routes.ref.Experiments.updateStateCode(code),fakeRequest().withJsonBody(Json.toJson(experimentUpdateForm)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		expUpdate=MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, code);
		assertThat(expUpdate.state.code).isEqualTo("F");
		assertThat(expUpdate.inputContainerSupportCodes).isNotEmpty();
		assertThat(expUpdate.outputContainerSupportCodes).isNotEmpty();
		
		containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("support.code", expUpdate.inputContainerSupportCodes) ).toList();
		assertThat(containers).isNotEmpty();
		for(Container container:containers){
			assertThat(container.state.code).isEqualTo("IS");
			assertThat(container.processTypeCode).isNull();
			assertThat(container.inputProcessCodes).isNull();
		}
		processes=MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME,Process.class,DBQuery.in("code", processCodes) ).toList();
		for(Process process:processes){
			assertThat(process.state.code).isEqualTo("F");
			assertThat(process.currentExperimentTypeCode).isNotNull();
			assertThat(code).isIn(process.experimentCodes);
			assertThat(expUpdate.outputContainerSupportCodes.get(0)).isIn(process.newContainerSupportCodes);
			assertThat(process.state.resolutionCodes).isNotNull();
		}
		
		List<Container> outPutContainers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("support.code", expUpdate.outputContainerSupportCodes) ).toList();
		assertThat(outPutContainers).isNotEmpty();
		assertThat(outPutContainers.size()).isEqualTo(expUpdate.atomicTransfertMethods.size());
		for(Container outContainer:outPutContainers){
			assertThat(expUpdate.typeCode).isIn(outContainer.fromExperimentTypeCodes);
			assertThat(outContainer.projectCodes).isNotEmpty();
			assertThat(outContainer.sampleCodes).isNotEmpty();
			assertThat(outContainer.state.code).isEqualTo("UA");
			assertThat(outContainer.processTypeCode).isNotEmpty();
			assertThat(outContainer.inputProcessCodes).isNull();
/*			for(String processCode:outContainer.inputProcessCodes){
				assertThat(processCode).isIn(processCodes);
			}*/
		}
	}
	
	//@Test
	public void retry(){
		//resetData();
		try {
			initData();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String code="PREPA-FLOWCELL-20150107_105554";
		
		// Experiment PREPA-FLOWCELL-20150107_105554 update state code "N" to "IP"
		ExperimentUpdateForm experimentUpdateForm = new ExperimentUpdateForm();
		experimentUpdateForm.nextStateCode = "IP";
		Result result = callAction(controllers.experiments.api.routes.ref.Experiments.updateStateCode(code),fakeRequest().withJsonBody(Json.toJson(experimentUpdateForm)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		Experiment expUpdate=MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, code);
		assertThat(expUpdate.state.code).isEqualTo("IP");
		assertThat(expUpdate.getAllOutPutContainerWhithInPutContainer()).isNotEmpty();
		
		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("support.code", expUpdate.inputContainerSupportCodes) ).toList();
		assertThat(containers).isNotEmpty();
		List<String> processCodes=new ArrayList<String>();
		for(Container container:containers){
			assertThat(container.state.code).isEqualTo("IU");
			InstanceHelpers.addCodesList(container.inputProcessCodes,processCodes);
		}
		List<Process> processes=MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME,Process.class,DBQuery.in("code", processCodes) ).toList();
		for(Process process:processes){
			assertThat(process.state.code).isEqualTo("IP");
			assertThat(code).isIn(process.experimentCodes);
		}
		
		
		// Experiment PREPA-FLOWCELL-20150107_105554 update state "IP" to state "F"
		experimentUpdateForm = new ExperimentUpdateForm();
		experimentUpdateForm.nextStateCode = "F";
		experimentUpdateForm.retry = true;
		experimentUpdateForm.processResolutionCodes=new ArrayList<String>();
		experimentUpdateForm.processResolutionCodes.add("processus-partiel");
		result = callAction(controllers.experiments.api.routes.ref.Experiments.updateStateCode(code),fakeRequest().withJsonBody(Json.toJson(experimentUpdateForm)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		expUpdate=MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, code);
		assertThat(expUpdate.state.code).isEqualTo("F");
		assertThat(expUpdate.inputContainerSupportCodes).isNotEmpty();
		assertThat(expUpdate.outputContainerSupportCodes).isNotEmpty();
		
		containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("support.code", expUpdate.inputContainerSupportCodes) ).toList();
		assertThat(containers).isNotEmpty();
		for(Container container:containers){
			assertThat(container.state.code).isEqualTo("A");
			assertThat(container.inputProcessCodes).isNotEmpty();
		}
		processes=MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME,Process.class,DBQuery.in("code", processCodes) ).toList();
		for(Process process:processes){
			assertThat(process.state.code).isEqualTo("IP");
			assertThat(process.currentExperimentTypeCode).isNotNull();
			assertThat(code).isIn(process.experimentCodes);
			assertThat(expUpdate.outputContainerSupportCodes.get(0)).isIn(process.newContainerSupportCodes);
		}
		
		List<Container> outPutContainers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("support.code", expUpdate.outputContainerSupportCodes) ).toList();
		assertThat(outPutContainers).isNotEmpty();
		assertThat(outPutContainers.size()).isEqualTo(expUpdate.atomicTransfertMethods.size());
		for(Container outContainer:outPutContainers){
			assertThat(expUpdate.typeCode).isIn(outContainer.fromExperimentTypeCodes);
			assertThat(outContainer.projectCodes).isNotEmpty();
			assertThat(outContainer.sampleCodes).isNotEmpty();
			assertThat(outContainer.state.code).isEqualTo("UA");
			assertThat(outContainer.processTypeCode).isNotEmpty();
			assertThat(outContainer.inputProcessCodes).isNotEmpty();
			for(String processCode:outContainer.inputProcessCodes){
				assertThat(processCode).isIn(processCodes);
			}
		}
	}
	
	
}
