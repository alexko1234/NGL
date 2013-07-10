package models.laboratory.experiment.instance;

import java.util.List;
import java.util.Map;

import play.data.validation.ValidationError;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;

public class ManytoOneContainer extends AtomicTransfertMethod{

	int inputNumber;
	List<ContainerUsed> inputContainerUseds;
	ContainerUsed outputContainerUsed;

	@Override
	public void createOutputContainerUsed(
			Map<String, PropertyDefinition> propertyDefinitions,
			Map<String, PropertyValue> propertyValues) {

	}

	@Override
	public void validate(Map<String, List<ValidationError>> errors) {
		outputContainerUsed.validate(errors);
		for(ContainerUsed containerUsed:inputContainerUseds){
			containerUsed.validate(errors);
		}
	}

	public List<ContainerUsed> getInputContainers(){
		return inputContainerUseds;
	}

}
