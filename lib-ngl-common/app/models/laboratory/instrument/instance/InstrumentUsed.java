package models.laboratory.instrument.instance;

import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.project.description.ProjectType;
import models.utils.HelperObjects;
import models.utils.ObjectSGBDReference;

import org.codehaus.jackson.annotate.JsonIgnore;

public class InstrumentUsed {

	// Reference Instrument code
	public String code;
	// Reference Instrument Category code
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
