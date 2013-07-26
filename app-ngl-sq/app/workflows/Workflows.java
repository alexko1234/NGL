package workflows;

import static validation.utils.ConstraintsHelper.addErrors;
import static validation.utils.ConstraintsHelper.getKey;
import static validation.utils.ConstraintsHelper.required;
import static validation.utils.ConstraintsHelper.validateProperties;

import java.util.List;
import java.util.Map;

import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import play.Logger;
import play.data.validation.ValidationError;
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
	public static void setExperimentStateCode(Experiment experiment, Map<String, List<ValidationError>>  errors){
		if(experiment.stateCode.equals("N")) {
			required(errors, experiment.typeCode, "typeCode");
		} else if(experiment.stateCode.equals("IP")) {
			required(errors, experiment.typeCode, "typeCode"); 
			required(errors, experiment.resolutionCode, "resolutionCode");
			required(errors, experiment.protocolCode, "protocolCode");
			required(errors, experiment.instrument.code, "instrument");
		} else if(experiment.stateCode.equals("F")) {
			required(errors, experiment.typeCode, "typeCode"); 
			required(errors, experiment.resolutionCode, "resolutionCode");
			required(errors, experiment.protocolCode, "protocolCode");
			required(errors, experiment.instrument.code, "instrument");
			required(errors, experiment.atomicTransfertMethods, "atomicTransfertMethods");
				
			validateProperties(errors, experiment.experimentProperties, experiment.getExperimentType().propertiesDefinitions, getKey(null,"nullPropertiesDefinitions"));
			
		}else{
			addErrors(errors,experiment.stateCode, getKey(null,"InvalidthisStateCode"));
		}	
	}
}
