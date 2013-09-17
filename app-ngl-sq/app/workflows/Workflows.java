package workflows;

import static validation.utils.ValidationHelper.addErrors;
import static validation.utils.ValidationHelper.getKey;
import static validation.utils.ValidationHelper.required;
import static validation.utils.ValidationHelper.validateProperties;

import java.util.List;
import java.util.Map;

import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import play.Logger;
import play.data.validation.ValidationError;
import validation.ContextValidation;
import controllers.Constants;
import fr.cea.ig.MongoDBDAO;

public class Workflows {

	/**
	 * Set a state of a container to A (Available)
	 * @param containerCode: the code of the container
	 */
	public static void setAvailable(String containerCode,String processTypeCode){
		Container container = MongoDBDAO.findByCode(Constants.CONTAINER_COLL_NAME, Container.class, containerCode);
		
		if(container != null && (container.stateCode.equals("IWP") || container.stateCode.equals("N"))){
			MongoDBDAO.updateSet(Constants.CONTAINER_COLL_NAME, container,"stateCode", "A");
			MongoDBDAO.updateSet(Constants.CONTAINER_COLL_NAME, container,"processTypeCode", processTypeCode);
		}
	}
	
	/**
	 * Set a state of a container to IU (In Use)
	 * @param containerCode: the code of the container
	 */
	public static void setInUse(List<ContainerUsed> inputContainers){
		for(ContainerUsed containerUsed:inputContainers){
			Container container = MongoDBDAO.findByCode(Constants.CONTAINER_COLL_NAME, Container.class, containerUsed.containerCode);
			
			if(container != null && (container.stateCode.equals("IWE"))){
				MongoDBDAO.updateSet(Constants.CONTAINER_COLL_NAME, container,"stateCode", "IU");
			}
		}	
	}
	
	/**
	 * Set a state of a container to IWE (In Waiting Experiment)
	 * @param inputContainers: list of containerUsed
	 */
	public static void setInWaitingExperiment(List<ContainerUsed> inputContainers){
		for(ContainerUsed containerUsed:inputContainers){
			Container container = MongoDBDAO.findByCode(Constants.CONTAINER_COLL_NAME, Container.class, containerUsed.containerCode);
			
			if(container != null && (container.stateCode.equals("A"))){
				MongoDBDAO.updateSet(Constants.CONTAINER_COLL_NAME, container,"stateCode", "IWE");
			}
		}	
	}
	
	/**
	 * Set a state of an experiment 
	 * @param experiment: the experiment, errors: the filledForm errors
	 */
	public static void setExperimentStateCode(Experiment experiment, ContextValidation  contextValidation){
		if(experiment.stateCode.equals("N")) {
			required(contextValidation,experiment.typeCode, "typeCode");
		} else if(experiment.stateCode.equals("IP")) {
			required(contextValidation, experiment.typeCode, "typeCode"); 
			required(contextValidation, experiment.resolutionCode, "resolutionCode");
			required(contextValidation, experiment.protocolCode, "protocolCode");
			required(contextValidation, experiment.instrument.code, "instrument");
			
		} else if(experiment.stateCode.equals("F")) {
			required(contextValidation, experiment.typeCode, "typeCode"); 
			required(contextValidation, experiment.resolutionCode, "resolutionCode");
			required(contextValidation, experiment.protocolCode, "protocolCode");
			required(contextValidation, experiment.instrument.code, "instrument");
			required(contextValidation, experiment.atomicTransfertMethods, "atomicTransfertMethods");
				
			validateProperties(contextValidation, experiment.experimentProperties, experiment.getExperimentType().propertiesDefinitions, "nullPropertiesDefinitions");
			
		}else{
			contextValidation.addErrors(experiment.stateCode, "InvalidthisStateCode");
		}	
	}
}
