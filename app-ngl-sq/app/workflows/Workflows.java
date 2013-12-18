package workflows;

import static validation.utils.ValidationHelper.addErrors;
import static validation.utils.ValidationHelper.getKey;
import static validation.utils.ValidationHelper.required;
import static validation.utils.ValidationHelper.validateProperties;

import java.util.List;
import java.util.Map;

import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.utils.InstanceConstants;
import play.Logger;
import play.data.validation.ValidationError;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

public class Workflows {

	/**
	 * Set a state of a container to A (Available)
	 * @param containerCode: the code of the container,processTypeCode: the code of the processType
	 */
	public static void setContainerAvailable(String containerCode,String processTypeCode){
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerCode);
		
		if(container != null && (container.stateCode.equals("IW-P") || container.stateCode.equals("N"))){
			MongoDBDAO.updateSet(InstanceConstants.CONTAINER_COLL_NAME, container,"stateCode", "A");
			MongoDBDAO.updateSet(InstanceConstants.CONTAINER_COLL_NAME, container,"processTypeCode", processTypeCode);
		}
	}
	
	/**
	 * Set a state of a container to A (Available)
	 * @param containerCode: the code of the container,processTypeCode: the code of the processType
	 */
	public static void setContainerAvailable(ContainerUsed containerUsed){
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerUsed.containerCode);
		
		if(container != null && (container.stateCode.equals("IW-P") || container.stateCode.equals("N"))){
			MongoDBDAO.updateSet(InstanceConstants.CONTAINER_COLL_NAME, container,"stateCode", "A");
		}
	}
	
	/**
	 * Set a state of a list of containerUsed to IU (In Use)
	 * @param List<ContainerUsed> inputContainers: the list of container
	 */
	public static void setContainerInUse(List<ContainerUsed> inputContainers){
		for(ContainerUsed containerUsed:inputContainers){
			Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerUsed.containerCode);
			
			if(container != null && (container.stateCode.equals("IWE"))){
				MongoDBDAO.updateSet(InstanceConstants.CONTAINER_COLL_NAME, container,"stateCode", "IU");
			}
		}	
	}
	
	/**
	 * Set final state of list if containerUsed to IS or UN (In Stock or Unavailable)
	 * @param List<ContainerUsed> inputContainers: the list of container
	 */
	public static void setContainersFinalState(List<ContainerUsed> inputContainers){
		for(ContainerUsed containerUsed:inputContainers){
			if(containerUsed != null && containerUsed.resolutionCode!= null){
				if(containerUsed.resolutionCode.equals("IS")){
					setContainerInStock(containerUsed);
				}else if(containerUsed.resolutionCode.equals("UN")){
					setContainerUnavailable(containerUsed);
				}else if(containerUsed.resolutionCode.equals("A")){
					setContainerAvailable(containerUsed);
				}
			}	
		}
	}
	
	/**
	 * Set a state of a container to IS (In Stock)
	 * @param containerCode: the code of the container
	 */
	public static void setContainerInStock(ContainerUsed containerUsed){
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerUsed.containerCode);
			
		if(container != null && (container.stateCode.equals("IU"))){
				MongoDBDAO.updateSet(InstanceConstants.CONTAINER_COLL_NAME, container,"stateCode", "IS");
		}
	}
	
	/**
	 * Set a state of a container to UA (Unavailable)
	 * @param containerCode: the code of the container
	 */
	public static void setContainerUnavailable(ContainerUsed containerUsed){
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerUsed.containerCode);
		
		if(container != null && (container.stateCode.equals("IU"))){
			MongoDBDAO.updateSet(InstanceConstants.CONTAINER_COLL_NAME, container,"stateCode", "UN");
		}
	}
	
	/**
	 * Set a state of a container to IWE (In Waiting Experiment)
	 * @param inputContainers: list of containerUsed
	 */
	public static void setContainersInWaitingExperiment(List<ContainerUsed> inputContainers){
		for(ContainerUsed containerUsed:inputContainers){
			Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerUsed.containerCode);
			
			if(container != null && (container.stateCode.equals("A"))){
				MongoDBDAO.updateSet(InstanceConstants.CONTAINER_COLL_NAME, container,"stateCode", "IWE");
			}
		}	
	}
	
	/**
	 * Set a state of an experiment 
	 * @param experiment: the experiment, errors: the filledForm errors
	 */
	public static void setExperimentState(Experiment experiment, String stateCode, ContextValidation ctxValidation){
		if(stateCode.equals("N")) {
			required(ctxValidation,experiment.typeCode, "typeCode");
		} else if(stateCode.equals("IP")) {
			required(ctxValidation, experiment.typeCode, "typeCode"); 
			required(ctxValidation, experiment.resolutionCode, "resolutionCode");
			required(ctxValidation, experiment.protocolCode, "protocolCode");
			required(ctxValidation, experiment.instrument.code, "instrument");
			
		} else if(stateCode.equals("F")) {
			required(ctxValidation, experiment.typeCode, "typeCode"); 
			required(ctxValidation, experiment.resolutionCode, "resolutionCode");
			required(ctxValidation, experiment.protocolCode, "protocolCode");
			required(ctxValidation, experiment.instrument.code, "instrument");
			required(ctxValidation, experiment.atomicTransfertMethods, "atomicTransfertMethods");

			for(int i=0;i<experiment.atomicTransfertMethods.size();i++){
				required(ctxValidation, experiment.atomicTransfertMethods.get(i).getOutputContainers(), "outputContainer");
			}
			
			ctxValidation.setRootKeyName("experimentProperties");
			validateProperties(ctxValidation, experiment.experimentProperties, experiment.getExperimentType().propertiesDefinitions);
			ctxValidation.removeKeyFromRootKeyName("experimentProperties");
			

		}else{
			ctxValidation.addErrors(experiment.stateCode, "InvalidStateCode");
		}	
	}
	
	public static void setExperimentInProcess(){
		
	}
}
