package models.laboratory.instrument.instance;

import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.utils.HelperObjects;

import org.codehaus.jackson.annotate.JsonIgnore;

public class InstrumentUsed {

	public String code;
	public String categoryCode;

	@JsonIgnore
	public Instrument getInstrument(){
		return new HelperObjects<Instrument>().getObject(Instrument.class, code, null);
	}

	@JsonIgnore
	public InstrumentCategory getInstrumentCategory(){
		return new HelperObjects<InstrumentCategory>().getObject(InstrumentCategory.class, categoryCode, null);
	}
}
