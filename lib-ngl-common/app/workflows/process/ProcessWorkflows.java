package workflows.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.instance.ProcessHelper;
import models.utils.instance.StateHelper;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import validation.ContextValidation;
import validation.processes.instance.ProcessValidationHelper;
import workflows.container.ContainerWorkflows;
import fr.cea.ig.MongoDBDAO;

public class ProcessWorkflows {


	public static void setProcessState(String processCode, State nextState, ContextValidation contextValidation) {
		Process process = MongoDBDAO.findOne(InstanceConstants.PROCESS_COLL_NAME, Process.class,
				DBQuery.is("code", processCode));

		if (process != null) {
			ProcessValidationHelper.validateStateCode(nextState.code, contextValidation);
			if (!contextValidation.hasErrors() && !nextState.code.equals(process.state)) {

				process.state = StateHelper.updateHistoricalNextState(process.state, nextState);
				process.traceInformation = StateHelper.updateTraceInformation(process.traceInformation, nextState);

				MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.is("code", process.code),
						DBUpdate.set("state", process.state).set("traceInformation", process.traceInformation));

			}
		}

	}

	public static void nextContainerStateFromNewProcesses(List<Process> processes,
			String processTypeCode, ContextValidation contextValidation) {

		String experimentCategoryCode=null;
		try {
			ProcessType pt = ProcessType.find.findByCode(processTypeCode);
			experimentCategoryCode=pt.firstExperimentType.category.code;

		} catch (DAOException e) {
			Logger.error("DAO error :"+e.getMessage(),e);
		}

		State nextState = new State();
		Set<String> processList=new HashSet<String>();

		nextState.code=ContainerWorkflows.getAvailableContainerStateFromExperimentCategory(experimentCategoryCode);

		Set<String> inputContainers=new HashSet<String>();
		for (Process process : processes) {
			inputContainers.add(process.inputContainerCode);
			processList.add(process.code);
		}

		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code",inputContainers)).toList();
		for(Container container : containers){
			ProcessHelper.updateContainer(container,processTypeCode, processList,contextValidation);
			ProcessHelper.updateContainerSupportFromContainer(container,contextValidation);
		}
		ContainerWorkflows.setContainerState(containers, nextState.code, contextValidation);
	}


	public static boolean setProcessState(List<Process> processes, String nextStateProcesses, Set<String> resolutions,
			ContextValidation ctxValidation) {

		Map<String,Set<String>> containersToUpdate=new HashMap<String,Set<String>>();
		Set<String> newContainerSupports=new HashSet<String>();
		State nextState=new State();
		nextState.user=ctxValidation.getUser();
		nextState.code=nextStateProcesses;
		nextState.resolutionCodes=resolutions;

		for(Process process:processes){

			process.state = StateHelper.updateHistoricalNextState(process.state, nextState);
			process.traceInformation = StateHelper.updateTraceInformation(process.traceInformation, nextState);

			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.is("code", process.code),
					DBUpdate.set("state", process.state).set("traceInformation", process.traceInformation));

			if(nextStateProcesses.equals("F")){
				Set<String> containerInputCodes=containersToUpdate.get(process.typeCode);
				if(containerInputCodes==null){
					containerInputCodes=new HashSet<String>();
				}
				containerInputCodes.add(process.inputContainerCode);
				containersToUpdate.put(process.typeCode,containerInputCodes);
				newContainerSupports.addAll(process.outputContainerSupportCodes);
			}

		}

		if(containersToUpdate.size()!=0){
			for(String processTypeCode :containersToUpdate.keySet()){
				for(String containerInPutCode:containersToUpdate.get(processTypeCode)){
					ProcessType processType;
					try {
						processType = ProcessType.find.findByCode(processTypeCode);
						MongoDBDAO
						.update(InstanceConstants.CONTAINER_COLL_NAME,
								Container.class,
								DBQuery.is("code", containerInPutCode).in("fromTransformationTypeCodes",
										processType.voidExperimentType.code),
										DBUpdate.unset("fromTransformationTypeCodes"));
						MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
								DBQuery.is("code",containerInPutCode), DBUpdate.unset("processCodes").unset("processTypeCodes"),
								true);

					} catch (DAOException e) {
						Logger.error("DAO error :"+e.getMessage(),e);
					}
					
				}
			}
		}
		
		if(newContainerSupports.size()!=0){
			List<String> stateCodes=new ArrayList<String>();
			stateCodes.add("UA");
			stateCodes.add("IS");
			stateCodes.add("F");
			stateCodes.add("IW-P");
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
					DBQuery.in("support.code", newContainerSupports).in("state.code",stateCodes),
					DBUpdate.unset("processCodes").unset("processTypeCodes"), true);
		}

		return true;
	}

}
