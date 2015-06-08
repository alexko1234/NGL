package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.Level;
import validation.ContextValidation;

public class OneToVoidContainer extends AtomicTransfertMethod {

	
	@Override
	public void validate(ContextValidation contextValidation) {
		contextValidation.putObject("level", Level.CODE.ContainerIn);
		for(ContainerUsed inputContainerUsed:inputContainerUseds){
			inputContainerUsed.validate(contextValidation);
		}
		contextValidation.removeObject("level");

	}

	@Override
	public ContextValidation createOutputContainerUsed(Experiment experiment,ContextValidation contextValidation) {
		return contextValidation;
	}

	@Override
	public List<ContainerUsed> getInputContainers() {
		List<ContainerUsed> cu = new ArrayList<ContainerUsed>();		
		cu.add(inputContainerUseds.get(0));
		return cu;
	}

	@Override
	public List<ContainerUsed> getOutputContainers() {
		return new ArrayList<ContainerUsed>();
	}

	@Override
	public ContextValidation saveOutputContainers(Experiment experiment, ContextValidation contextValidation) {
		return contextValidation;
	}

}
