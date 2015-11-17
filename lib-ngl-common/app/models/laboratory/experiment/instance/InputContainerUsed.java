package models.laboratory.experiment.instance;

import java.util.Set;

import models.laboratory.common.description.Level;
import models.laboratory.container.instance.Container;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.experiment.instance.InputContainerValidationHelper;

public class InputContainerUsed extends AbstractContainerUsed {
	
	
	public Double percentage; //percentage of input in the final output
	public Set<String> fromExperimentTypeCodes; //used in rules
	

	public InputContainerUsed() {
		super();
		
	}
	
	public InputContainerUsed(String code) {
		super(code);
		
	}

	
	@Override
	public void validate(ContextValidation contextValidation) {
		Container container = InputContainerValidationHelper.validateExistInstanceCode(contextValidation, code, Container.class, InstanceConstants.CONTAINER_COLL_NAME, true);
		InputContainerValidationHelper.compareInputContainerWithContainer(contextValidation, this, container);
		
		InputContainerValidationHelper.validateVolume(contextValidation, volume);
		InputContainerValidationHelper.validateConcentration(contextValidation, concentration);
		InputContainerValidationHelper.validateQuantity(contextValidation, quantity);
		
		InputContainerValidationHelper.validatePercentage(contextValidation, percentage);
		InputContainerValidationHelper.validateExperimentProperties(contextValidation, experimentProperties, Level.CODE.ContainerIn);
		InputContainerValidationHelper.validateInstrumentProperties(contextValidation, instrumentProperties, Level.CODE.ContainerIn);
	}

	
}
