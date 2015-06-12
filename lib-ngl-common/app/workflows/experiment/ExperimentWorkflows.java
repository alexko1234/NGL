package workflows.experiment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.ExperimentUpdateState;
import models.laboratory.processes.description.ExperimentTypeNode;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.instance.ExperimentHelper;
import models.utils.instance.StateHelper;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import play.libs.F.Promise;
import validation.ContextValidation;
import validation.container.instance.ContainerValidationHelper;
import validation.experiment.instance.ExperimentValidationHelper;
import validation.processes.instance.ProcessValidationHelper;
import workflows.container.ContainerWorkflows;
import workflows.process.ProcessWorkflows;
import fr.cea.ig.MongoDBDAO;

public class ExperimentWorkflows {

	/**
	 * Set a state of an experiment
	 * 
	 * @param experiment
	 *            : the experiment, errors: the filledForm errors
	 */
/*public static void setExperimentState(Experiment experiment, State nextState, ContextValidation ctxValidation,
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
					ContainerWorkflows.nextOutputContainerState(experiment, ctxValidation, stopProcess, retry,processResolutionCodes);
				}

			}

			if (!ctxValidation.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class,
						DBQuery.is("code", experiment.code),
						DBUpdate.set("state", experiment.state).set("traceInformation", experiment.traceInformation));
			}

			if (!ctxValidation.hasErrors()) {
				ContainerWorkflows.nextInputContainerState(experiment, ctxValidation, stopProcess, retry, processResolutionCodes);
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

	public static boolean doQC(Experiment experiment) {
		try {
			ExperimentTypeNode experimentTypeNode = ExperimentTypeNode.find.findByCode(experiment.typeCode);
			return experimentTypeNode.doQualityControl;
		} catch (DAOException e) {
			throw new RuntimeException();
		}

	}

*/
	/***********************************************************************/
	public static void setExperimentState(Experiment experiment, State nextState,ContextValidation contextValidation) {

		if (nextState.code.equals("IP")) {
			try {
				ExperimentHelper.generateOutputContainerUsed(experiment, contextValidation);
				if (!contextValidation.hasErrors()) {
					MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, experiment);
				}
			} catch (DAOException e) {
				throw new RuntimeException();
			}
		} else if (nextState.code.equals("F")) {
			try {
				ExperimentHelper.saveOutputContainerUsed(experiment, contextValidation);
			} catch (DAOException e) {
				throw new RuntimeException();
			}
		}

		experiment.traceInformation = StateHelper.getUpdateTraceInformation(experiment.traceInformation,
				contextValidation.getUser());
		experiment.state = StateHelper.updateHistoricalNextState(experiment.state, nextState);
		experiment.state = nextState;

		MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class,
				DBQuery.is("code", experiment.code),
				DBUpdate.set("state", experiment.state).set("traceInformation", experiment.traceInformation));

	}

	public static void setExperimentUpdateState(Experiment exp,ExperimentUpdateState experimentUpdateState,
			ContextValidation ctxValidation) {
		//Search Process, Containers and validate state code
		if(experimentUpdateState.nextStateProcesses!=null && !ctxValidation.hasErrors()){
			//Exclu les processus qui sont deja a cette etat
			experimentUpdateState.processes=new HashSet<>(MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class,DBQuery.in("experimentCodes", exp.code).notEquals("state.code",experimentUpdateState.nextStateProcesses)).toList());
			ProcessValidationHelper.validateStateCode(experimentUpdateState.nextStateProcesses, ctxValidation);
		}

		if(experimentUpdateState.nextStateInputContainers!=null && !ctxValidation.hasErrors()){
			experimentUpdateState.inputContainers=new HashSet<>(MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("support.code",exp.inputContainerSupportCodes)).toList());
			ContainerValidationHelper.validateStateCode(experimentUpdateState.nextStateInputContainers,ctxValidation);
		}

		if(experimentUpdateState.nextStateOutputContainers!=null && !ctxValidation.hasErrors()){
			experimentUpdateState.outputContainers= new HashSet<>(MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("support.code",exp.outputContainerSupportCodes)).toList());
			ContainerValidationHelper.validateStateCode(experimentUpdateState.nextStateOutputContainers,ctxValidation);
		}

		if (!ctxValidation.hasErrors()) {

			if(experimentUpdateState.nextStateInputContainers!=null){
				ContainerWorkflows.setContainerState(experimentUpdateState.inputContainers, experimentUpdateState.nextStateInputContainers, ctxValidation);
			}
			
			if(experimentUpdateState.nextStateOutputContainers!=null){
				ContainerWorkflows.setContainerState(experimentUpdateState.outputContainers,experimentUpdateState.nextStateOutputContainers, ctxValidation);
			}

			if(experimentUpdateState.nextStateProcesses!=null){
				ProcessWorkflows.setProcessState(experimentUpdateState.processes, experimentUpdateState.nextStateProcesses, experimentUpdateState.processResolutionCodes, ctxValidation);
			}
		}
	}

}
