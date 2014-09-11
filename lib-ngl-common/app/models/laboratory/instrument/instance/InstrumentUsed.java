package models.laboratory.instrument.instance;


import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.utils.HelperObjects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import validation.ContextValidation;
import validation.DescriptionValidationHelper;
import validation.IValidation;
import validation.utils.BusinessValidationHelper;

public class InstrumentUsed implements IValidation {

	public String code;
	//TODO ?? ces infos sont pour l'instant dans Experiment ???
	//public String typeCode;
	//public Map<String,PropertyValue> properties
	public String categoryCode;
	public String inContainerSupportCategoryCode;
	public String outContainerSupportCategoryCode;
	public String typeCode;


	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		DescriptionValidationHelper.validationInstrumentCode(code,contextValidation);
		DescriptionValidationHelper.validationInstrumentCategoryCode(categoryCode,contextValidation);
		//type
		//inContainerSupportCategoryCode
		//outContainerSupportCategoryCode
	}

}
