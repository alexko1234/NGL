package workflows;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.laboratory.common.description.Institute;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TransientState;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.description.ExperimentTypeNode;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.instance.ExperimentHelper;
import models.utils.instance.StateHelper;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import akka.actor.ActorRef;
import akka.actor.Props;
import play.Logger;
import play.Play;
import play.libs.Akka;
import rules.services.RulesActor;
import rules.services.RulesActor6;
import rules.services.RulesMessage;
import validation.ContextValidation;
import validation.container.instance.ContainerValidationHelper;
import validation.experiment.instance.ExperimentValidationHelper;
import validation.processes.instance.ProcessValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class Workflows {

	private static final String ruleWorkflowSQ = "workflow";
	private static ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor6.class));

	/**
	 * Set a state of an experiment
	 * 
	 * @param experiment
	 *            : the experiment, errors: the filledForm errors
	 */
	public static void setExperimentState(Experiment experiment, State nextState, ContextValidation ctxValidation,
			boolean stopProcess, boolean retry, List<String> processResolutionCodes) {

		ctxValidation.getContextObjects().put("stateCode", nextState.code);
		ExperimentValidationHelper.validateState(experiment.typeCode, nextState, ctxValidation);

		// il fau peut etre valider tout l'experiment quand elle passe à "F"
		ExperimentValidationHelper.validateNewState(experiment, ctxValidation);

		if (!ctxValidation.hasErrors() && !nextState.code.equals(experiment.state)) {

			experiment.traceInformation = StateHelper.getUpdateTraceInformation(experiment.traceInformation,
					ctxValidation.getUser());
			experiment.state = StateHelper.updateHistoricalNextState(experiment.state, nextState);
			experiment.state = nextState;

			if (experiment.state.code.equals("IP")) {
				try {
					ExperimentHelper.generateOutputContainerUsed(experiment, ctxValidation);
					if (!ctxValidation.hasErrors()) {
						MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, experiment);
					}
				} catch (DAOException e) {
					throw new RuntimeException();
				}
			} else if (experiment.state.code.equals("F")) {
				try {
					ExperimentHelper.saveOutputContainerUsed(experiment, ctxValidation);
				} catch (DAOException e) {
					throw new RuntimeException();
				}
				Logger.debug("Apres saveOutputContainerUsed");
				if (!ctxValidation.hasErrors()) {
					nextOutputContainerState(experiment, ctxValidation, stopProcess, retry,processResolutionCodes);
				}

			}

			if (!ctxValidation.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class,
						DBQuery.is("code", experiment.code),
						DBUpdate.set("state", experiment.state).set("traceInformation", experiment.traceInformation));
			}

			if (!ctxValidation.hasErrors()) {
				nextInputContainerState(experiment, ctxValidation, stopProcess, retry, processResolutionCodes);
			}
		}
	}

	public static void nextExperimentState(Experiment experiment, ContextValidation contextValidation,
			boolean stopProcess, boolean retry) {
		State state = StateHelper.cloneState(experiment.state);

		if (experiment.state == null || experiment.state.code.equals("")) {
			state.code = "N";
		} else if (experiment.state.code.equals("N")) {
			state.code = "IP";
		} else if (experiment.state.code.equals("IP")) {
			state.code = "F";
		}

		setExperimentState(experiment, state, contextValidation, stopProcess, retry, null);
	}

	public static void nextInputContainerState(Experiment experiment, ContextValidation contextValidation,
			boolean stopProcess, boolean retry, List<String> processResolutionCodes) {
		nextInputContainerState(experiment, experiment.getAllInPutContainer(), contextValidation, stopProcess, retry, processResolutionCodes);
	}

	public static void nextInputContainerState(Experiment experiment, List<ContainerUsed> containersIn,
			ContextValidation contextValidation, boolean stopProcess, boolean retry,List<String> processResolutionCodes) {
		State state = new State();
		state.date = new Date();
		state.user = contextValidation.getUser();

		if (experiment.state.code.equals("N")) {
			state.code = "IW-E";
		} else if (experiment.state.code.equals("IP")) {
			state.code = "IU";
		} else if (experiment.state.code.equals("F")) {
			if (!stopProcess && !retry) {
				if (experiment.categoryCode.equals("qualitycontrol")) {
					state.code = "IW-V";
				} else {
					// Mettre à jour l'etat en fonction du volume restant
					state.code = "IS";
				}
			} else if (stopProcess) {
				Logger.info("STOP PROCESS");
				state.code = "IS";
			} else if (retry) {
				for (ContainerUsed c : containersIn) {
					Container container = MongoDBDAO.findOne(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
							DBQuery.is("code", c.code));
					//Duplicate container in experiment
					if(container.state.code.equals("IU")){
						Workflows.previousContainerState(c,container, experiment.typeCode, contextValidation, 2);
					}
				}
			}
		}

		if (state.code != null && !retry) {
			setContainerState(containersIn, experiment.typeCode, state, contextValidation, stopProcess, false, processResolutionCodes);
			// Il faut mettre à jour le state du container dans
			// l'experiment.atomicTransfereMethod
		}
	}

	public static void nextOutputContainerState(Experiment experiment, ContextValidation contextValidation,
			boolean stopProcess, boolean retry, List<String> processResolutionCodes) {
		contextValidation.putObject("workflow", true);
		for (ContainerUsed containerUsed : experiment.getAllOutPutContainerWhithInPutContainer()) {

			State nextState = new State();
			nextState.user = experiment.traceInformation.modifyUser;
			if (!stopProcess && !retry) {
				if (experiment.categoryCode.equals("transformation")) {
					if (experiment.state.code.equals("F") && doQC(experiment)) {
						nextState.code = "A-QC";
					}/*
					 * else if(experiment.state.code.equals("F") && doPurif()){
					 * nextState.code="A-PURIF"; }else
					 * if(experiment.state.code.equals("F") && doTransfert()){
					 * nextState.code="A-TRANSFERT"; }
					 */else if (experiment.state.code.equals("F") && endOfProcess(containerUsed, experiment.typeCode)) {
						if (experiment.typeCode.equals("opgen-depot")) {
							nextState.code = "F";
						} else {
							nextState.code = "IW-P";
						}
					} else {
						nextState.code = "A";
					}
				}

				if (experiment.categoryCode.equals("purification") || experiment.categoryCode.equals("transfert")) {
					if (experiment.state.code.equals("F")) {
						nextState.code = "IS";
					}
				}
			} else {
				if (experiment.state.code.equals("F")) {
					nextState.code = "UA";
				}
			}
			if (nextState.code != null && containerUsed != null) {
				setContainerState(containerUsed.code, experiment.typeCode, nextState, contextValidation, stopProcess,
						retry, processResolutionCodes);
			}
		}
		contextValidation.removeObject("workflow");
	}

	private static boolean endOfProcess(ContainerUsed containerUsed, String experimentTypeCode) {
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
				containerUsed.code);
		ProcessType processType;
		try {
			Logger.debug("Container :" + containerUsed.code);
			if (container.getCurrentProcesses() == null)
				return true;
			processType = ProcessType.find.findByCode(container.getCurrentProcesses().get(0).typeCode);
			if (processType.lastExperimentType.code.equals(experimentTypeCode)) {
				return true;
			} else {
				return false;
			}

		} catch (DAOException e) {
			throw new RuntimeException();
		}
	}	

	private static boolean endOfProcess(String processCode, String experimentTypeCode) {
		Process process = MongoDBDAO.findByCode(InstanceConstants.PROCESS_COLL_NAME, Process.class, processCode);
		ProcessType processType;
		try {
			Logger.debug("Process " + processCode);
			processType = ProcessType.find.findByCode(process.typeCode);
			if (processType.lastExperimentType.code.equals(experimentTypeCode)) {
				return true;
			} else {
				return false;
			}

		} catch (DAOException e) {
			throw new RuntimeException();
		}
	}

	private static boolean nextExperiment(String typeCode) {
		List<ExperimentType> experimentTypes;
		try {
			experimentTypes = ExperimentType.find.findNextExperimentTypeForAnExperimentTypeCode(typeCode);
			if (experimentTypes != null && experimentTypes.size() > 0) {
				return true;
			} else {
				return false;
			}

		} catch (DAOException e) {
			throw new RuntimeException();
		}
	}

	private static boolean doQC(Experiment experiment) {
		try {
			ExperimentTypeNode experimentTypeNode = ExperimentTypeNode.find.findByCode(experiment.typeCode);
			return experimentTypeNode.doQualityControl;
		} catch (DAOException e) {
			throw new RuntimeException();
		}

	}

	public static void nextProcessState(Container container, String experimentTypeCode,
			ContextValidation contextValidation, boolean stopProcess, boolean retry, List<String> processResolutionCodes) {
		if (container.inputProcessCodes != null) {
			for (String processCode : container.inputProcessCodes) {

				State processState = new State();
				processState.date = new Date();
				processState.user = contextValidation.getUser();

				if (container.state.code.equals("IU") && checkProcessState("N", processCode)) {
					processState.code = "IP";
				} else if ((container.state.code.equals("UA") || container.state.code.equals("IS"))
						&& !retry
						&& checkProcessState("IP", processCode)
						&& (stopProcess || (experimentTypeCode != null && endOfProcess(processCode, experimentTypeCode)))) {
					processState.code = "F";
					processState.resolutionCodes = processResolutionCodes;
				}

				if (processState.code != null) {
					setProcessState(processCode, processState, contextValidation);
				}
				
				if(checkProcessState("F", processCode)){
						ProcessType processType;
						try {
							Process process=MongoDBDAO.findByCode(InstanceConstants.PROCESS_COLL_NAME, Process.class,processCode);
							processType = ProcessType.find.findByCode(process.typeCode);
							MongoDBDAO
									.update(InstanceConstants.CONTAINER_COLL_NAME,
											Container.class,
											DBQuery.is("code", process.containerInputCode).in("fromExperimentTypeCodes",
													processType.voidExperimentType.code),
											DBUpdate.unset("fromExperimentTypeCodes"));
							MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
									DBQuery.is("code", process.containerInputCode), DBUpdate.unset("inputProcessCodes"),
									true);
							List<String> stateCodes=new ArrayList<String>();
							stateCodes.add("UA");
							stateCodes.add("IS");
							stateCodes.add("F");
							MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
									DBQuery.in("support.code", process.newContainerSupportCodes).in("state.code",stateCodes),
									DBUpdate.unset("inputProcessCodes"), true);

						} catch (DAOException e) {
						}
					}	
					
				}

			}
	}

	private static boolean checkProcessState(String stateCode, String processCode) {
		return MongoDBDAO.checkObjectExist(InstanceConstants.PROCESS_COLL_NAME, Process.class,
				DBQuery.is("code", processCode).is("state.code", stateCode));
	}

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

				// Process F, reset fromExperimentTypeCodes if Collab's
				// container

			}
			
			

		}

	}

	public static void stopProcess(String processCode, ContextValidation contextValidation,List<String> processResolutionCodes) {
		Process process = MongoDBDAO.findByCode(InstanceConstants.PROCESS_COLL_NAME, Process.class, processCode);
		if (process != null) {
			State state = new State();
			state.code = "IS";
			contextValidation.setUpdateMode();
			Workflows.setContainerState(process.containerInputCode, process.currentExperimentTypeCode, state,
					contextValidation, true, false,processResolutionCodes);
		}
	}

	public static void setContainerState(String containerCode, String experimentTypeCode, State nextState,
			ContextValidation contextValidation) {
		setContainerState(containerCode, experimentTypeCode, nextState, contextValidation, false, false, null);
	}

	public static void setContainerState(String containerCode, String experimentTypeCode, State nextState,
			ContextValidation contextValidation, boolean stopProcess, boolean retry, List<String> processResolutionCodes) {
		Container container = MongoDBDAO.findOne(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
				DBQuery.is("code", containerCode));
		if (container != null) {
			setContainerState(container, experimentTypeCode, nextState, contextValidation, stopProcess, retry, processResolutionCodes);
		} else {
			Logger.error("Container " + containerCode + " not exists");
		}
	}

	public static void setContainerState(Container container, String experimentTypeCode, State nextState,
			ContextValidation contextValidation, boolean stopProcess, boolean retry,List<String> processResolutionCodes) {
		String lastStateCode = container.state.code;
		container.traceInformation = StateHelper.updateTraceInformation(container.traceInformation, nextState);
		// Validate state for Container
		contextValidation.addKeyToRootKeyName("container");
		ContainerValidationHelper.validateStateCode(container, contextValidation);
		contextValidation.removeKeyFromRootKeyName("container");
		if (!contextValidation.hasErrors() && !nextState.code.equals(lastStateCode)) {
			container.state = StateHelper.updateHistoricalNextState(container.state, nextState);
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
					DBQuery.is("code", container.code),
					DBUpdate.set("state", container.state).set("traceInformation", container.traceInformation));
			 rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"),ruleWorkflowSQ, container),null);
		}
		container.state = nextState;
		nextContainerSupportState(container, contextValidation);
		nextProcessState(container, experimentTypeCode, contextValidation, stopProcess, retry, processResolutionCodes);
	}

	private static void nextContainerSupportState(Container container, ContextValidation contextValidation) {
		State nextState = new State(container.state.code, container.state.user);
		// Pour le moment des qu'une container change d'etat sont support à la
		// meme etat
		setContainerSupportState(container.support.code, nextState, contextValidation);
	}

	public static void setContainerSupportState(String code, State nextState, ContextValidation contextValidation) {

		ContainerSupport containerSupport = MongoDBDAO.findOne(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME,
				ContainerSupport.class, DBQuery.is("code", code));

		if (containerSupport == null) {
			Logger.error("ContainerSupport " + containerSupport + " not exists");
		}

		if (!contextValidation.hasErrors() && !nextState.code.equals(containerSupport.state.code)) {

			containerSupport.state = StateHelper.updateHistoricalNextState(containerSupport.state, nextState);
			containerSupport.traceInformation = StateHelper.updateTraceInformation(containerSupport.traceInformation,
					nextState);

			MongoDBDAO.update(
					InstanceConstants.CONTAINER_SUPPORT_COLL_NAME,
					Container.class,
					DBQuery.is("code", containerSupport.code),
					DBUpdate.set("state", containerSupport.state).set("traceInformation",
							containerSupport.traceInformation));

		}

	}

	public static void setContainerState(List<ContainerUsed> containersUsed, String experimentTypeCode, State state,
			ContextValidation contextValidation, boolean stopProcess, boolean retry, List<String> processResolutionCodes) {
		for (ContainerUsed containerUsed : containersUsed) {
			setContainerState(containerUsed.code, experimentTypeCode, state, contextValidation, stopProcess, retry, processResolutionCodes);
		}
	}

	public static void previousContainerState(List<ContainerUsed> containersIn, String experimentCode,
			String experimentTypeCode, ContextValidation contextValidation) {

		for (ContainerUsed container : containersIn) {
			previousContainerState(container, experimentTypeCode, contextValidation);
			// remove the current experiment in the process and the experiment
			// in the list of experiment
			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,
					DBQuery.is("currentExperimentTypeCode", experimentTypeCode)
							.is("containerInputCode", container.code), DBUpdate.set("currentExperimentTypeCode", "")
							.pull("experimentCodes", experimentCode));
		}
	}

	public static void previousContainerState(ContainerUsed containersIn, String experimentTypeCode,
			ContextValidation contextValidation) {
		Container container = MongoDBDAO.findOne(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
				DBQuery.is("code", containersIn.code));

		TransientState previousTransientState = container.state.historical.get(container.state.historical.size() - 2);
		State previousState = new State();
		previousState.code = previousTransientState.code;
		previousState.user = contextValidation.getUser();

		setContainerState(container, experimentTypeCode, previousState, contextValidation, false, false, null);
	}

	public static void previousContainerState(ContainerUsed containersIn, Container container, String experimentTypeCode,
			ContextValidation contextValidation, int previousNumber) {

		TransientState previousTransientState = container.state.historical.get(container.state.historical.size()
				- (previousNumber + 1));
		State previousState = new State();
		previousState.code = previousTransientState.code;

		setContainerState(container, experimentTypeCode, previousState, contextValidation, false, true, null);
	}

	public static void nextContainerState(List<Process> processes, String experimentTypeCode,
			String experimentCategoryCode, ContextValidation contextValidation) {

		for (Process process : processes) {
			nextContainerState(process, experimentTypeCode, experimentCategoryCode, contextValidation);
		}
	}

	public static void nextContainerState(Process process, String experimentTypeCode, String experimentCategoryCode,
			ContextValidation contextValidation) {
		State nextState = new State();
		if (process.state != null && process.state.code.equals("N")) {
			if (experimentCategoryCode.equals(ExperimentCategory.CODE.qualitycontrol.name())) {
				nextState.code = "A-QC";
			} else if (experimentCategoryCode.equals(ExperimentCategory.CODE.purification.name())) {
				nextState.code = "A-PF";
			} else if (experimentCategoryCode.equals(ExperimentCategory.CODE.transfert.name())) {
				nextState.code = "A-TF";
			} else {
				nextState.code = "A";
			}
		}

		setContainerState(process.containerInputCode, experimentTypeCode, nextState, contextValidation, false, false, null);

	}

}
