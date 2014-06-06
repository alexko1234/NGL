package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.List;

import validation.ContextValidation;

public class OneToVoidContainer extends AtomicTransfertMethod {

	public ContainerUsed inputContainerUsed;
	
	@Override
	public void validate(ContextValidation contextValidation) {
		inputContainerUsed.validate(contextValidation);

	}

	@Override
	public void createOutputContainerUsed(Experiment experiment) {
	}

	@Override
	public List<ContainerUsed> getInputContainers() {
		List<ContainerUsed> cu = new ArrayList<ContainerUsed>();
		cu.add(inputContainerUsed);
		return cu;
	}

	@Override
	public List<ContainerUsed> getOutputContainers() {
		return new ArrayList<ContainerUsed>();
	}

	@Override
	public ContextValidation saveOutputContainers(Experiment experiment) {
		return new ContextValidation();
	}

}
