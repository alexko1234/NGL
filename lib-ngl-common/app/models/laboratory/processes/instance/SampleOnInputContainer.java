package models.laboratory.processes.instance;

import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.run.instance.SampleOnContainer;
import validation.ContextValidation;
import validation.IValidation;
import validation.processes.instance.SampleOnInputContainerValidationHelper;

public class SampleOnInputContainer extends SampleOnContainer implements IValidation{
	
	public PropertySingleValue mesuredVolume;
	public PropertySingleValue mesuredConcentration;
	public PropertySingleValue mesuredQuantity;
	

	@Override
	public void validate(ContextValidation contextValidation) {		
		SampleOnInputContainerValidationHelper.validateExistSampleCode(this, contextValidation);
		SampleOnInputContainerValidationHelper.validateSampleCategoryCode(this, contextValidation);
		SampleOnInputContainerValidationHelper.validateContainerSupportCode(this, contextValidation);
		SampleOnInputContainerValidationHelper.validateExistContainerCode(this, contextValidation);
		
	}


	@Override
	public String toString() {
		return super.toString().replace(']', ' ') + ", mesuredVolume=" + mesuredVolume + ", mesuredConcentration="
				+ mesuredConcentration + ", mesuredQuantity=" + mesuredQuantity + "]";
	}
	
	

}
