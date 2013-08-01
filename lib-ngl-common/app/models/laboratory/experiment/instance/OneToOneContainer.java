package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import validation.utils.ContextValidation;


import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.utils.InstanceConstants;
import models.utils.instance.ContainerHelper;
import fr.cea.ig.MongoDBDAO;

public class OneToOneContainer extends AtomicTransfertMethod{

	public ContainerUsed inputContainerUsed;
	public ContainerUsed outputContainerUsed;

	
	@Override
	public List<Container> createOutputContainerUsed(Map<String,PropertyDefinition> propertyDefinitions, Map<String,PropertyValue> propertyValues,Experiment experiment) {
		
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, inputContainerUsed.containerCode);
		Container outputContainer = new Container();
		
		outputContainer.stateCode="A";
		ContainerHelper.addContent(container, outputContainer, experiment);
		
		ContainerHelper.copyProperties(propertyDefinitions,propertyValues,outputContainer);			

		ContainerHelper.addContainerSupport(outputContainer, experiment);
		
		ContainerHelper.generateCode(outputContainer);
		
		this.outputContainerUsed = new ContainerUsed(outputContainer);
		
		List<Container> containers = new ArrayList<Container>();
		containers.add(outputContainer);
		return containers;
	}
	
	@Override
	public void validate(ContextValidation contextValidation) {
		inputContainerUsed.validate(contextValidation);
		outputContainerUsed.validate(contextValidation);
	}
	
	public List<ContainerUsed> getInputContainers(){
		List<ContainerUsed> cu = new ArrayList<ContainerUsed>();
		cu.add(inputContainerUsed);
		return cu;
	}
}
