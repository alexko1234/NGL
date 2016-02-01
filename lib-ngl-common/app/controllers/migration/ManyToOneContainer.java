package controllers.migration;

import models.laboratory.container.description.ContainerSupportCategory;
import validation.ContextValidation;
import validation.experiment.instance.AtomicTransfertMethodValidationHelper;

public class ManyToOneContainer extends AtomicTransfertMethodOld{


	public ManyToOneContainer(){
		super();
	}

	@Override
	public void updateOutputCodeIfNeeded(ContainerSupportCategory outputCsc, String supportCode) {
		
	}

	@Override
	public void validate(ContextValidation contextValidation) {
		super.validate(contextValidation);
		
	}




}
