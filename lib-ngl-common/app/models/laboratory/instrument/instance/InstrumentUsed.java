package models.laboratory.instrument.instance;


import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.utils.HelperObjects;

import org.codehaus.jackson.annotate.JsonIgnore;

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

	@JsonIgnore
	public Instrument getInstrument(){
		return new HelperObjects<Instrument>().getObject(Instrument.class, code);
	}

	@JsonIgnore
	public InstrumentCategory getInstrumentCategory(){
		return new HelperObjects<InstrumentCategory>().getObject(InstrumentCategory.class, categoryCode);
	}

	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		DescriptionValidationHelper.validationInstrumentCode(code,contextValidation);
		DescriptionValidationHelper.validationInstrumentCategoryCode(code,contextValidation);
	}

}
