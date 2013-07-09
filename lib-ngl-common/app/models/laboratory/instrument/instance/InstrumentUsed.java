package models.laboratory.instrument.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.utils.HelperObjects;
import models.utils.IValidation;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;
import validation.utils.BusinessValidationHelper;

public class InstrumentUsed implements IValidation {

	public String code;
	public String categoryCode;

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
	public void validate(Map<String, List<ValidationError>> errors) {
		BusinessValidationHelper.validateRequiredDescriptionCode(errors, code, "instrument.code", Instrument.find);
		BusinessValidationHelper.validateRequiredDescriptionCode(errors, categoryCode, "instrument.categoryCode", InstrumentCategory.find);
	}

}
