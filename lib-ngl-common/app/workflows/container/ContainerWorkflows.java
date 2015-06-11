package workflows.container;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TransientState;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.instance.StateHelper;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import play.Play;
import play.libs.Akka;
import rules.services.RulesActor6;
import rules.services.RulesMessage;
import validation.ContextValidation;
import validation.container.instance.ContainerValidationHelper;
import workflows.experiment.ExperimentWorkflows;
import workflows.process.ProcessWorkflows;
import akka.actor.ActorRef;
import akka.actor.Props;
import fr.cea.ig.MongoDBDAO;

public class ContainerWorkflows {

	private static final String ruleWorkflowSQ = "workflow";
	private static ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor6.class));

/*
	public static void nextInputContainerState(Experiment experiment, ContextValidation contextValidation,
			boolean stopProcess, boolean retry, List<String> processResolutionCodes) {
		ContainerWorkflows.nextInputContainerState(experiment, experiment.getAllInPutContainer(), contextValidation, stopProcess, retry, processResolutionCodes);
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
						ContainerWorkflows.previousContainerState(c,container, experiment.typeCode, contextValidation, 2);
					}
				}
			}
		}

		if (state.code != null && !retry) {
			ContainerWorkflows.setContainerState(containersIn, experiment.typeCode, state, contextValidation, stopProcess, false, processResolutionCodes);
			// Il faut mettre à jour le state du container dans
			// l'experiment.atomicTransfereMethod
		}
	}
*/
	/*public static void nextOutputContainerState(Experiment experiment, ContextValidation contextValidation,
			boolean stopProcess, boolean retry, List<String> processResolutionCodes) {
		contextValidation.putObject("workflow", true);
		for (ContainerUsed containerUsed : experiment.getAllOutPutContainerWhithInPutContainer()) {

			State nextState = new State();
			nextState.user = experiment.traceInformation.modifyUser;
			if (!stopProcess && !retry) {
				if (experiment.categoryCode.equals("transformation")) {
					if (experiment.state.code.equals("F") && ExperimentWorkflows.doQC(experiment)) {
						nextState.code = "A-QC";
					}/*
					 * else if(experiment.state.code.equals("F") && doPurif()){
					 * nextState.code="A-PURIF"; }else
					 * if(experiment.state.code.equals("F") && doTransfert()){
					 * nextState.code="A-TRANSFERT"; }
					 *//*else if (experiment.state.code.equals("F") && ProcessWorkflows.endOfProcess(containerUsed, experiment.typeCode)) {
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
				ContainerWorkflows.setContainerState(containerUsed.code, experiment.typeCode, nextState, contextValidation, stopProcess,
						retry, processResolutionCodes);
			}
		}
		contextValidation.removeObject("workflow");
	}
*/
	/*
	public static void setContainerState(String containerCode, String experimentTypeCode, State nextState,
			ContextValidation contextValidation) {
		ContainerWorkflows.setContainerState(containerCode, experimentTypeCode, nextState, contextValidation, false, false, null);
	}

	public static void setContainerState(String containerCode, String experimentTypeCode, State nextState,
			ContextValidation contextValidation, boolean stopProcess, boolean retry, List<String> processResolutionCodes) {
		Container container = MongoDBDAO.findOne(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
				DBQuery.is("code", containerCode));
		if (container != null) {
			ContainerWorkflows.setContainerState(container, experimentTypeCode, nextState, contextValidation, stopProcess, retry, processResolutionCodes);
		} else {
			Logger.error("Container " + containerCode + " not exists");
		}
	}

	public static void setContainerState(Container container, String experimentTypeCode, State nextState,
			ContextValidation contextValidation, boolean stopProcess, boolean retry,List<String> processResolutionCodes) {
		String lastStateCode = container.state.code;
		container.traceInformation = StateHelper.updateTraceInformation(container.traceInformation, nextState);
		container.state = StateHelper.updateHistoricalNextState(container.state, nextState);
		// Validate state for Container
		contextValidation.addKeyToRootKeyName("container");
		ContainerValidationHelper.validateStateCode(container, contextValidation);
		contextValidation.removeKeyFromRootKeyName("container");
		if (!contextValidation.hasErrors() && !nextState.code.equals(lastStateCode)) {
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
					DBQuery.is("code", container.code),
					DBUpdate.set("state", container.state).set("traceInformation", container.traceInformation));
			ContainerWorkflows.rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"),ContainerWorkflows.ruleWorkflowSQ, container),null);
		}
		container.state = nextState;
		ContainerWorkflows.nextContainerSupportState(container, contextValidation);
		ProcessWorkflows.nextProcessState(container, experimentTypeCode, contextValidation, stopProcess, retry, processResolutionCodes);
	}

	public static void nextContainerSupportState(Container container, ContextValidation contextValidation) {
		State nextState = new State(container.state.code, container.state.user);
		// Pour le moment des qu'une container change d'etat sont support à la
		// meme etat
		ContainerWorkflows.setContainerSupportState(container.support.code, nextState, contextValidation);
	}
*/
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
	
	
	public static void setContainerState(Container container, State nextState, ContextValidation contextValidation){
		String lastStateCode=container.state.code;
		container.traceInformation = StateHelper.updateTraceInformation(container.traceInformation, nextState);
		container.state = StateHelper.updateHistoricalNextState(container.state, nextState);
		// Validate state for Container
		contextValidation.addKeyToRootKeyName("container");
		ContainerValidationHelper.validateStateCode(container, contextValidation);
		contextValidation.removeKeyFromRootKeyName("container");
		if (!contextValidation.hasErrors() && !nextState.code.equals(lastStateCode)) {
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
					DBQuery.is("code", container.code),
					DBUpdate.set("state", container.state).set("traceInformation", container.traceInformation));
			ContainerWorkflows.rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"),ContainerWorkflows.ruleWorkflowSQ, container),null);
		}
		
		setContainerSupportState(container.support.code, nextState, contextValidation);
	}
/*
	public static void setContainerState(List<ContainerUsed> containersUsed, String experimentTypeCode, State state,
			ContextValidation contextValidation, boolean stopProcess, boolean retry, List<String> processResolutionCodes) {
		for (ContainerUsed containerUsed : containersUsed) {
			setContainerState(containerUsed.code, experimentTypeCode, state, contextValidation, stopProcess, retry, processResolutionCodes);
		}
	}

	public static void previousContainerState(List<ContainerUsed> containersIn, String experimentCode,
			String experimentTypeCode, ContextValidation contextValidation) {

		for (ContainerUsed container : containersIn) {
			ContainerWorkflows.previousContainerState(container, experimentTypeCode, contextValidation);
			// remove the current experiment in the process and the experiment
			// in the list of experiment
			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,
					DBQuery.is("currentExperimentTypeCode", experimentTypeCode)
					.is("containerInputCode", container.code), DBUpdate.unset("currentExperimentTypeCode")
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
*/
	/**********************************************************/

	public static boolean setContainerState(Set<Container> containers,String nextState,ContextValidation contextValidation){

		Set<String> supporContainerSet=new HashSet<String>();

		State nextStateContainer=new State();
		nextStateContainer.code=nextState;
		nextStateContainer.user=contextValidation.getUser();

		if (!contextValidation.hasErrors()) {
			
			for(Container container:containers){
				container.state = StateHelper.updateHistoricalNextState(container.state, nextStateContainer);
				container.traceInformation = StateHelper.updateTraceInformation(container.traceInformation,
						nextStateContainer);	
				container.state.code=nextState;
				
				//Logger.debug("Container update "+container.code + " state " +container.state.code +" nextState "+nextState);
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
						DBQuery.is("code", container.code),
						DBUpdate.set("state", container.state).set("traceInformation", container.traceInformation));

				supporContainerSet.add(container.support.code);
			}

			ContainerWorkflows.rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"),ContainerWorkflows.ruleWorkflowSQ, containers),null);
			
			List<ContainerSupport> containerSupports=MongoDBDAO.find(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class,DBQuery.in("code",supporContainerSet).notEquals("state.code",nextState)).toList();
			for(ContainerSupport containerSupport:containerSupports){
				containerSupport.state = StateHelper.updateHistoricalNextState(containerSupport.state, nextStateContainer);
				containerSupport.traceInformation = StateHelper.updateTraceInformation(containerSupport.traceInformation,
						nextStateContainer);
				MongoDBDAO.update(
						InstanceConstants.CONTAINER_SUPPORT_COLL_NAME,
						Container.class,
						DBQuery.is("code", containerSupport.code),
						DBUpdate.set("state", containerSupport.state).set("traceInformation",
								containerSupport.traceInformation));

			}


		}
		return true;
	}
	public static String getNextContainerStateFromExperimentCategory(String categoryCode) {
		String nextContainerState=null;
		if(categoryCode.equals(ExperimentCategory.CODE.transformation.name())){
			nextContainerState="A-TM";
		}else if(categoryCode.equals(ExperimentCategory.CODE.transfert.name())){
			nextContainerState="A-TF";
		}else if(categoryCode.equals(ExperimentCategory.CODE.qualitycontrol.name())){
			nextContainerState="A-QC";
		}else if(categoryCode.equals(ExperimentCategory.CODE.purification.name())){
			nextContainerState="A-PF";
		}
		return nextContainerState;
	}
}
