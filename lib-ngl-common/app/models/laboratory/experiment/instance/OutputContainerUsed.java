package models.laboratory.experiment.instance;

import models.laboratory.common.description.Level;
import validation.ContextValidation;
import validation.experiment.instance.ContainerUsedValidationHelper;

public class OutputContainerUsed extends AbstractContainerUsed{
	
	public OutputContainerUsed() {
		super();
		
	}
	
	public OutputContainerUsed(String code) {
		super(code);
		
	}
	
	@Override
	public void validate(ContextValidation contextValidation) {
		
		ContainerUsedValidationHelper.validateOutputContainerCode(code, contextValidation);
		ContainerUsedValidationHelper.validateLocationOnSupportOnContainer(locationOnContainerSupport, contextValidation);
		ContainerUsedValidationHelper.validateOutputContainerCategoryCode(categoryCode, contextValidation);
		ContainerUsedValidationHelper.validateOutputContents(contents, contextValidation);
		ContainerUsedValidationHelper.validateVolume(volume, contextValidation);
		ContainerUsedValidationHelper.validateConcentration(concentration, contextValidation);
		ContainerUsedValidationHelper.validateQuantity(quantity, contextValidation);
		
		ContainerUsedValidationHelper.validateExperimentProperties(experimentProperties, Level.CODE.ContainerOut, contextValidation);
		ContainerUsedValidationHelper.validateInstrumentProperties(instrumentProperties, Level.CODE.ContainerOut, contextValidation);
	}

	
}
