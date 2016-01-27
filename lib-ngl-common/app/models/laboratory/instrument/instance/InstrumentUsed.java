package models.laboratory.instrument.instance;


import validation.ContextValidation;
import validation.IValidation;
import validation.experiment.instance.InstrumentUsedValidationHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class InstrumentUsed implements IValidation {

	public String code;
	public String typeCode;
	public String categoryCode;
	public String inContainerSupportCategoryCode;
	public String outContainerSupportCategoryCode;
	

	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		InstrumentUsedValidationHelper.validationTypeCode(typeCode, contextValidation);
		InstrumentUsedValidationHelper.validationCode(code,contextValidation);
		InstrumentUsedValidationHelper.validationCategoryCode(categoryCode,contextValidation);
		InstrumentUsedValidationHelper.validationInContainerSupportCategoryCode(inContainerSupportCategoryCode,contextValidation);
		InstrumentUsedValidationHelper.validationOutContainerSupportCategoryCode(outContainerSupportCategoryCode,contextValidation);		
	}

}
