package models.laboratory.experiment.instance;

import java.util.Set;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.experiment.instance.ContainerUsedValidationHelper;

public class InputContainerUsed extends AbstractContainerUsed {
	
	
	public Double percentage; //percentage of input in the final output
	public Set<String> fromExperimentTypeCodes; //used in rules
	
	//TODO Must be remove after refactoring just keep for backward compatibility
	public State state;
	
	public InputContainerUsed() {
		super();
		
	}
	
	public InputContainerUsed(String code) {
		super(code);
		
	}

	
	@Override
	public void validate(ContextValidation contextValidation) {
		Container container = ContainerUsedValidationHelper.validateExistInstanceCode(contextValidation, code, Container.class, InstanceConstants.CONTAINER_COLL_NAME, true);
		ContainerUsedValidationHelper.compareInputContainerWithContainer(this, container, contextValidation);
		ContainerUsedValidationHelper.validateInputContainerCategoryCode(categoryCode, contextValidation);
		ContainerUsedValidationHelper.validateVolume(volume, contextValidation);
		ContainerUsedValidationHelper.validateConcentration(concentration, contextValidation);
		ContainerUsedValidationHelper.validateQuantity(quantity, contextValidation);
		
		ContainerUsedValidationHelper.validatePercentage(percentage, contextValidation);
		ContainerUsedValidationHelper.validateExperimentProperties(experimentProperties, Level.CODE.ContainerIn, contextValidation);
		ContainerUsedValidationHelper.validateInstrumentProperties(instrumentProperties, Level.CODE.ContainerIn, contextValidation);
	}

	
}
