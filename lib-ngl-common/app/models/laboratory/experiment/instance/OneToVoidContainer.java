package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.Level;
import validation.ContextValidation;
import validation.experiment.instance.AtomicTransfertMethodValidationHelper;

public class OneToVoidContainer extends AtomicTransfertMethod {

	
	@Override
	public void validate(ContextValidation contextValidation) {
		super.validate(contextValidation);
		AtomicTransfertMethodValidationHelper.validateOneInputContainer(inputContainerUseds, contextValidation);
		AtomicTransfertMethodValidationHelper.validateVoidOutputContainer(outputContainerUseds, contextValidation);
		
	}

	@Override
	public ContextValidation createOutputContainerUsed(Experiment experiment,ContextValidation contextValidation) {
		return contextValidation;
	}
	

	@Override
	public ContextValidation saveOutputContainers(Experiment experiment, ContextValidation contextValidation) {
		return contextValidation;
	}

}
