package models.laboratory.experiment.instance;

import models.laboratory.common.description.Level;
import validation.ContextValidation;
import validation.experiment.instance.InputContainerValidationHelper;

public class OutputContainerUsed extends AbstractContainerUsed{
	
	public OutputContainerUsed() {
		super();
		
	}
	
	public OutputContainerUsed(String code) {
		super(code);
		
	}
	
	@Override
	public void validate(ContextValidation contextValidation) {
		
		InputContainerValidationHelper.validateVolume(contextValidation, volume);
		InputContainerValidationHelper.validateConcentration(contextValidation, concentration);
		InputContainerValidationHelper.validateQuantity(contextValidation, quantity);
		
		InputContainerValidationHelper.validateExperimentProperties(contextValidation, experimentProperties, Level.CODE.ContainerIn);
		InputContainerValidationHelper.validateInstrumentProperties(contextValidation, instrumentProperties, Level.CODE.ContainerIn);
	}

	
}
