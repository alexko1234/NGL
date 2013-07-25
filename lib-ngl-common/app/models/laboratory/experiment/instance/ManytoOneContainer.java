package models.laboratory.experiment.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;

public class ManytoOneContainer extends AtomicTransfertMethod{

	public int inputNumber;
	
	public List<ContainerUsed> inputContainerUseds;
	public ContainerUsed outputContainerUsed;

	@Override
	public List<Container> createOutputContainerUsed(
			Map<String, PropertyDefinition> propertyDefinitions,
			Map<String, PropertyValue> propertyValues) {
		return null;
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
