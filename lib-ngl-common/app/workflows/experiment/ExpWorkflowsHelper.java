package workflows.experiment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.instance.ExperimentHelper;

import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import validation.ContextValidation;
import workflows.container.ContainerWorkflows;
import workflows.process.ProcessWorkflows;
import fr.cea.ig.MongoDBDAO;

public class ExpWorkflowsHelper {
	
	
	public static void updateXCodes(Experiment exp) {
		Set<String> sampleCodes = new HashSet<String>();
		Set<String> projectCodes  = new HashSet<String>();
		Set<String> inputContainerSupportCodes  = new HashSet<String>();
		
		List<String> inputContainerCodes = exp.getAllInputContainers().stream().map((ContainerUsed c) -> c.code).collect(Collectors.toList());
		List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code", inputContainerCodes)).toList();
		for(Container container:containers){			
			if(CollectionUtils.isNotEmpty(container.projectCodes)){
				projectCodes.addAll(container.projectCodes);
			}	
			if(CollectionUtils.isNotEmpty(container.sampleCodes)){
				sampleCodes.addAll(container.sampleCodes);
			}
			inputContainerSupportCodes.add(container.support.code);
		}	
		
		exp.projectCodes = projectCodes;		
		exp.sampleCodes = sampleCodes;
		exp.inputContainerSupportCodes = inputContainerSupportCodes;		
		
		MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class,DBQuery.is("code", exp.code)
				,DBUpdate.set("projectCodes", exp.projectCodes).set("sampleCodes", exp.sampleCodes).set("inputContainerSupportCodes", exp.inputContainerSupportCodes));
	}

	
	public static void updateContainersAndProcesses(Experiment exp, ContextValidation ctxVal) {
		List<String> inputContainerCodes = exp.getAllInputContainers().stream().map((ContainerUsed c) -> c.code).collect(Collectors.toList());
		if(inputContainerCodes.size() > 0){
			List<Container> inputContainers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code", inputContainerCodes)).toList();
			ContainerWorkflows.setContainerState(inputContainers,"IW-E", ctxVal);
			
			Set<String> processCodes = inputContainers.stream().map((Container c)-> c.inputProcessCodes).flatMap(Set::stream).collect(Collectors.toSet());			
			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME,Process.class,
					DBQuery.in("code", processCodes).notEquals("state.code", "F"), 
					DBUpdate.set("currentExperimentTypeCode", exp.typeCode).push("experimentCodes", exp.code),true);		
		}
	}
	
	public static void updateContainersAndProcessesState(Experiment exp, ContextValidation ctxVal, String containerStateCode, String processStateCode) {
		List<String> inputContainerCodes = exp.getAllInputContainers().stream().map((ContainerUsed c) -> c.code).collect(Collectors.toList());
		if(inputContainerCodes.size() > 0){
			List<Container> inputContainers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code", inputContainerCodes).notEquals("state.code", containerStateCode)).toList();
			ContainerWorkflows.setContainerState(inputContainers,containerStateCode, ctxVal);
			
			Set<String> processCodes = inputContainers.stream().map((Container c)-> c.inputProcessCodes).flatMap(Set::stream).collect(Collectors.toSet());			
			List<Process> processes=MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class,DBQuery.in("code", processCodes).notEquals("state.code", processStateCode)).toList();
			ProcessWorkflows.setProcessState(processes, processStateCode, null, ctxVal);
		}
	}
	
	
	public static void updateAddContainersToExperiment(Experiment expFromUser, ContextValidation ctxVal) {
		Experiment expFromDB = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, expFromUser.code);
		
		List<String> newContainerCodes = getNewContainerCodes(expFromDB, expFromUser);
		if(newContainerCodes.size() > 0){
			List<Container> newContainers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code", newContainerCodes)).toList();
			ContainerWorkflows.setContainerState(newContainers,"IW-E", ctxVal);
			
			Set<String> processCodes = newContainers.stream().map((Container c)-> c.inputProcessCodes).flatMap(Set::stream).collect(Collectors.toSet());			
			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, 
					DBQuery.in("code", processCodes).notEquals("state.code", "F"), 
					DBUpdate.set("currentExperimentTypeCode", expFromDB.typeCode).push("experimentCodes", expFromDB.code));			
		}
	}
	
	public static void updateRemoveContainersFromExperiment(Experiment expFromUser,
			ContextValidation ctxVal) {
		Experiment expFromDB = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, expFromUser.code);
		
		List<String> removeContainerCodes = getRemoveContainerCodes(expFromDB, expFromUser);
		if(removeContainerCodes.size() > 0){
			List<Container> removeContainers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code", removeContainerCodes)).toList();			
			String nextContainerState=ContainerWorkflows.getAvailableContainerStateFromExperimentCategory(expFromDB.categoryCode);			
			ContainerWorkflows.setContainerState(removeContainers, nextContainerState, ctxVal);
			
			Set<String> processCodes = removeContainers.stream().map((Container c)-> c.inputProcessCodes).flatMap(Set::stream).collect(Collectors.toSet());			
			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, 
					DBQuery.in("code", processCodes).notEquals("state.code", "F"), 
					DBUpdate.unset("currentExperimentTypeCode").pull("experimentCodes", expFromDB.code));
			
		}
	}

	




	private static List<String> getNewContainerCodes(Experiment expFromDB, Experiment expFromUser) {
		List<String> containerCodesFromDB = expFromDB.getAllInputContainers().stream().map((ContainerUsed c) -> c.code).collect(Collectors.toList());
		List<String> containerCodesFromUser = expFromUser.getAllInputContainers().stream().map((ContainerUsed c) -> c.code).collect(Collectors.toList());
		
		List<String> newContainersCodes = new ArrayList<String>();
		for(String codeFromDB:containerCodesFromUser){
			if(!containerCodesFromDB.contains(codeFromDB)){
				newContainersCodes.add(codeFromDB);
			}
		}		
		return newContainersCodes;
	}




	private static List<String> getRemoveContainerCodes(Experiment expFromDB, Experiment expFromUser) {
		List<String> containerCodesFromDB = expFromDB.getAllInputContainers().stream().map((ContainerUsed c) -> c.code).collect(Collectors.toList());
		List<String> containerCodesFromUser = expFromUser.getAllInputContainers().stream().map((ContainerUsed c) -> c.code).collect(Collectors.toList());
		
		List<String> removeContainersCodes = new ArrayList<String>();
		for(String codeFromDB:containerCodesFromDB){
			if(!containerCodesFromUser.contains(codeFromDB)){
				removeContainersCodes.add(codeFromDB);
			}
		}
		
		return removeContainersCodes;
	}

}
