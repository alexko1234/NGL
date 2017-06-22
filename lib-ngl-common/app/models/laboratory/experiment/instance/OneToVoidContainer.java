package models.laboratory.experiment.instance;

import models.laboratory.container.description.ContainerSupportCategory;
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
	public void updateOutputCodeIfNeeded(ContainerSupportCategory outputCsc,
			String supportCode) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void removeOutputContainerCode(){
		// TODO Auto-generated method stub
	}

}
