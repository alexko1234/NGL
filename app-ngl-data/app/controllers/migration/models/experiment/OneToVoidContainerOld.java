package controllers.migration.models.experiment;


import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.Level;
import models.laboratory.experiment.instance.ContainerUsed;
import validation.ContextValidation;
import controllers.migration.models.ExperimentOld;

public class OneToVoidContainerOld extends AtomicTransfertMethodOld {

	public ContainerUsed inputContainerUsed;
	
	@Override
	public void validate(ContextValidation contextValidation) {
		contextValidation.putObject("level", Level.CODE.ContainerIn);
		inputContainerUsed.validate(contextValidation);
		contextValidation.removeObject("level");

	}

	@Override
	public ContextValidation createOutputContainerUsed(ExperimentOld experiment,ContextValidation contextValidation) {
		return contextValidation;
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
	public ContextValidation saveOutputContainers(ExperimentOld experiment, ContextValidation contextValidation) {
		return contextValidation;
	}

}
