package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.container.instance.Container;
import validation.ContextValidation;

public class OneToVoidContainer extends AtomicTransfertMethod {

	public ContainerUsed inputContainerUsed;
	
	@Override
	public void validate(ContextValidation contextValidation) {
		inputContainerUsed.validate(contextValidation);

	}

	@Override
	public List<Container> createOutputContainerUsed(Experiment experiment) {
		return null;
	}

	@Override
	public List<ContainerUsed> getInputContainers() {
		List<ContainerUsed> cu = new ArrayList<ContainerUsed>();
		cu.add(inputContainerUsed);
		return cu;
	}

	@Override
	public List<ContainerUsed> getOutputContainers() {
		return null;
	}

}
