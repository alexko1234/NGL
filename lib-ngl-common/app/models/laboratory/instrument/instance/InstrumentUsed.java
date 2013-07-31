package models.laboratory.instrument.instance;


import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.utils.HelperObjects;
import models.utils.IValidation;

import org.codehaus.jackson.annotate.JsonIgnore;


import validation.utils.BusinessValidationHelper;
import validation.utils.ContextValidation;

public class InstrumentUsed implements IValidation {

	public String code;
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
	public void validate(ContextValidation contextErrors) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextErrors.errors, code, "instrument.code", Instrument.find);
		BusinessValidationHelper.validateRequiredDescriptionCode(contextErrors.errors, categoryCode, "instrument.categoryCode", InstrumentCategory.find);
	}

}
