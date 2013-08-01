package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import validation.utils.ContextValidation;


import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.description.ExperimentType;
import models.utils.InstanceConstants;
import models.utils.instance.ContainerHelper;
import net.vz.mongodb.jackson.DBQuery;
import fr.cea.ig.MongoDBDAO;

public class ManytoOneContainer extends AtomicTransfertMethod{

	public int inputNumber;

	public List<ContainerUsed> inputContainerUseds;
	public ContainerUsed outputContainerUsed;

	@Override
	public List<Container> createOutputContainerUsed(
			Map<String, PropertyDefinition> propertyDefinitions,
			Map<String, PropertyValue> propertyValues,Experiment experiment) {


		List<String> containerCodes= new ArrayList<String>();
		for(int i=0;i<inputContainerUseds.size();i++){
			containerCodes.add(inputContainerUseds.get(i).containerCode);
		}

		List<Container> inputContainers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.in("code", containerCodes)).toList();

		List<Container> containers = new ArrayList<Container>();
		Container container = new Container();
		container.stateCode="A";

		for(int i=0;i<inputContainers.size();i++){
			ContainerHelper.addContent(inputContainers.get(i), container,experiment);
		}

		ContainerHelper.copyProperties(propertyDefinitions,propertyValues,container);			
		ContainerHelper.addContainerSupport(container, experiment);
		ContainerHelper.generateCode(container);
		
		outputContainerUsed=new ContainerUsed(container);
		containers.add(container);
		return containers;
	}

	@Override
	public void validate(ContextValidation contextValidation) {
		outputContainerUsed.validate(contextValidation);
		for(ContainerUsed containerUsed:inputContainerUseds){
			containerUsed.validate(contextValidation);
		}
	}

	public List<ContainerUsed> getInputContainers(){
		return inputContainerUseds;
	}

}
