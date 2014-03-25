package workflows;

import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.instance.StateHelper;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import play.Logger;
import validation.ContextValidation;
import validation.container.instance.ContainerValidationHelper;
import validation.experiment.instance.ExperimentValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class Workflows {

	/**
	 * Set a state of a container to A (Available)
	 * @param containerCode: the code of the container,processTypeCode: the code of the processType
	 * @deprecated
	 */
	public static void setContainerAvailable(String containerCode,String processTypeCode){
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerCode);

		if ( container != null && container.state != null && (container.state.code.equals("IW-P") || container.state.code.equals("N")) ) {
			MongoDBDAO.updateSet(InstanceConstants.CONTAINER_COLL_NAME, container,"state.code", "A");
			MongoDBDAO.updateSet(InstanceConstants.CONTAINER_COLL_NAME, container,"processTypeCode", processTypeCode);
		}
	}

	/**
	 * Set a state of a container to A (Available)
	 * @param containerCode: the code of the container,processTypeCode: the code of the processType
	 * @deprecated
	 */
	public static void setContainerAvailable(ContainerUsed containerUsed){
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerUsed.containerCode);

		if ( container != null && container.state != null && (container.state.code.equals("IW-P") || container.state.code.equals("N")) ) {
			MongoDBDAO.updateSet(InstanceConstants.CONTAINER_COLL_NAME, container,"state.code", "A");
		}
	}

	/**
	 * Set a state of a list of containerUsed to IU (In Use)
	 * @param List<ContainerUsed> inputContainers: the list of container
	 * @deprecated
	 */
	public static void setContainerInUse(List<ContainerUsed> inputContainers){
		for(ContainerUsed containerUsed:inputContainers){
			Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerUsed.containerCode);

			if(container != null && container.state != null && (container.state.code.equals("IWE"))){
				MongoDBDAO.updateSet(InstanceConstants.CONTAINER_COLL_NAME, container,"state.code", "IU");
			}
		}	
	}

	/**
	 * Set final state of list if containerUsed to IS or UN (In Stock or Unavailable)
	 * @param List<ContainerUsed> inputContainers: the list of container
	 * @deprecated
	 */
	public static void setContainersFinalState(List<ContainerUsed> containers){
		for(ContainerUsed containerUsed:containers){
			if(containerUsed != null && containerUsed.resolutionCodes!= null){
				if(containerUsed.resolutionCodes.equals("IS")){
					setContainerInStock(containerUsed);
				}else if(containerUsed.resolutionCodes.equals("UN")){
					setContainerUnavailable(containerUsed);
				}else if(containerUsed.resolutionCodes.equals("A")){
					setContainerAvailable(containerUsed);
				}
			}	
		}
	}

	/**
	 * Set a state of a container to IS (In Stock)
	 * @param containerCode: the code of the container
	 * @deprecated
	 */
	public static void setContainerInStock(ContainerUsed containerUsed){
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerUsed.containerCode);

		if (container != null && container.state != null && (container.state.code.equals("IU")) ) {
			MongoDBDAO.updateSet(InstanceConstants.CONTAINER_COLL_NAME, container,"state.code", "IS");
		}
	}

	/**
	 * Set a state of a container to UA (Unavailable)
	 * @param containerCode: the code of the container
	 * @deprecated
	 */
	public static void setContainerUnavailable(ContainerUsed containerUsed){
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerUsed.containerCode);

		if (container != null && container.state != null && (container.state.code.equals("IU")) ) {
			MongoDBDAO.updateSet(InstanceConstants.CONTAINER_COLL_NAME, container,"state.code", "UN");
		}
	}

	/**
	 * Set a state of a container to IWE (In Waiting Experiment)
	 * @param inputContainers: list of containerUsed
	 * @deprecated
	 */
	public static void setContainersInWaitingExperiment(List<ContainerUsed> inputContainers){
		for(ContainerUsed containerUsed:inputContainers){
			Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerUsed.containerCode);

			if (container != null && container.state != null && (container.state.code.equals("A")) ) {
				MongoDBDAO.updateSet(InstanceConstants.CONTAINER_COLL_NAME, container,"state.code", "IWE");
			}
		}	
	}

	/**
	 * Set a state of an experiment 
	 * @param experiment: the experiment, errors: the filledForm errors
	 */
	public static void setExperimentState(Experiment experiment, State nextState, ContextValidation ctxValidation){
		
		ctxValidation.getContextObjects().put("stateCode", nextState.code);
		ExperimentValidationHelper.validateState(experiment.typeCode, nextState, ctxValidation);

		//il fau peut etre valider tout l'experiment quand il passe à "F"
		ExperimentValidationHelper.validateNewState(experiment, ctxValidation);

		if(!ctxValidation.hasErrors() && !nextState.code.equals(experiment.stateCode)){
			
			InstanceHelpers.updateTraceInformation(experiment.traceInformation);  
			//experiment.state = StateHelper.updateHistoricalNextState(experiment.state, nextState);
			experiment.stateCode=nextState.code;
			
			if(!ctxValidation.hasErrors()){
				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME,  Run.class, 
						DBQuery.is("code", experiment.code),
						//DBUpdate.set("state", experiment.state).set("traceInformation",experiment.traceInformation));
						DBUpdate.set("stateCode", experiment.stateCode).set("traceInformation",experiment.traceInformation));
			}
			
			nextInPutContainerState(experiment, ctxValidation);
			//TODO
			//nextOutPutContainerState(experiment, ctxValidation);
			//TODO
			//nextProcessState(experiment, ctxValidation);
		}

		
		
		

		//Validation en Java class in context
		/*if(nextState.equals("N")) {
			required(ctxValidation,experiment.typeCode, "typeCode");
		} else if(nextState.equals("IP")) {
			required(ctxValidation, experiment.typeCode, "typeCode"); 
			required(ctxValidation, experiment.protocolCode, "protocolCode");
			required(ctxValidation, experiment.instrument.code, "instrument");
			required(ctxValidation, experiment.atomicTransfertMethods, "atomicTransfertMethods");

		} else if(nextState.equals("F")) {
			required(ctxValidation, experiment.typeCode, "typeCode");
			required(ctxValidation, experiment.resolutionCodes, "resolutionCodes");
			required(ctxValidation, experiment.protocolCode, "protocolCode");
			required(ctxValidation, experiment.instrument.code, "instrument");
			required(ctxValidation, experiment.atomicTransfertMethods, "atomicTransfertMethods");

			for(int i=0;i<experiment.atomicTransfertMethods.size();i++){
				if(!(experiment.atomicTransfertMethods.get(i) instanceof OneToVoidContainer)){
					required(ctxValidation, experiment.atomicTransfertMethods.get(i).getOutputContainers(), "outputContainer");
				}
			}

			ctxValidation.setRootKeyName("experimentProperties");
			validateProperties(ctxValidation, experiment.experimentProperties, experiment.getExperimentType().propertiesDefinitions);
			ctxValidation.removeKeyFromRootKeyName("experimentProperties");


		}else{
			ctxValidation.addErrors(experiment.stateCode, "InvalidStateCode");
		}
		*/
	}

	public static void nextInPutContainerState(Experiment experiment,ContextValidation contextValidation){
		State state=new State();
		state.date=new Date();
		state.user=InstanceHelpers.getUser();
		
		if(experiment.stateCode.equals("N")){
			state.code= "IW-E"; 
		}else if(experiment.stateCode.equals("IP")){
			state.code= "IU";
		}else if(experiment.stateCode.equals("F")){
			state.code= "A";
		}else {
			Logger.error("No input container state defined for this experiment"+experiment.code);
		}
		//Mettre une map pour les messages d'erreur
		setContainerState(experiment.getAllInPutContainer(), state, contextValidation);
	}
	
	//TODO
	public static String nextOutPutContainerState(Experiment experiment,ContextValidation contextValidation){
		if(experiment.stateCode.equals("N")){
			return "IW-E"; 
		}else if(experiment.stateCode.equals("IP")){
			return "";
		}else if(experiment.stateCode.equals("F")){
			//TODO return from evaluation
			return "A";
		}else {
			Logger.error("No output container state defined for this experiment"+experiment.code);
		}
		return null;
	}
	
	//TODO
	public static String nextProcessState(Experiment experiment,ContextValidation contextValidation){
		if(experiment.stateCode.equals("IP")){
			return "IP";
		}else if(experiment.stateCode.equals("F")){
			//Si seulement dernière experiment
			return "F";
		}
		return null;
	}

	//TODO
	public static void setContainerState(String containerCode,State nextState,ContextValidation contextValidation){
		Container container =MongoDBDAO.findOne(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("code", containerCode));
		
		if(container==null){
			Logger.error("Container "+containerCode+" not exists in experiment");
		} 
		//Validate state for Container
		ContainerValidationHelper.validateStateCode(nextState.code, contextValidation);
		if(!contextValidation.hasErrors() && !nextState.code.equals(container.state.code)){

			TraceInformation traceInformation=new TraceInformation();
			InstanceHelpers.updateTraceInformation(traceInformation);
			StateHelper.updateHistoricalNextState(container.state,nextState);
			
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME,  Container.class, 
					DBQuery.is("code", container.code),
					DBUpdate.set("state", nextState).set("traceInformation",traceInformation));
		}	
	}

	public static void setContainerState(List<ContainerUsed> containersUsed,State state,ContextValidation contextValidation){
		for(ContainerUsed containerUsed:containersUsed){
			Workflows.setContainerState(containerUsed.containerCode, state, contextValidation);
		}
	}

	//TODO
	public static void setProcessState(Process process,State state,ContextValidation contextValidation){
		
	}
	
}
