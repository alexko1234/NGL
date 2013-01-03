package models.instance.instrument;

import org.codehaus.jackson.annotate.JsonIgnore;

import utils.refobject.ObjectSGBDReference;

import models.description.experiment.Instrument;
import models.description.experiment.InstrumentCategory;

public class InstrumentUsed {

	// Reference Instrument code
	public String code;
	// Reference Instrument Category code
	public String categoryCode;

	@JsonIgnore
	public Instrument getInstrument(){

		try {
			return new ObjectSGBDReference<Instrument>(Instrument.class,code).getObject();
		} catch (Exception e) {
			// TODO
		}
		return null;
	}

	@JsonIgnore
	public InstrumentCategory getInstrumentCategory(){

		try {
			return new ObjectSGBDReference<InstrumentCategory>(InstrumentCategory.class,categoryCode).getObject();
		} catch (Exception e) {
			// TODO
		}
		return null;	
	}
}
