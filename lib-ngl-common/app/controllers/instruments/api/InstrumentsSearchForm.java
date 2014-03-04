package controllers.instruments.api;

import java.util.List;

import org.codehaus.jackson.annotate.JsonUnwrapped;

import models.laboratory.instrument.description.InstrumentQueryParams;
import controllers.ListForm;

public class InstrumentsSearchForm extends ListForm {
	
	public String typeCode;
	public List<String> typeCodes;
	
	public String categoryCode;
	public List<String> categoryCodes;
	
	public Boolean active;
	
	public InstrumentQueryParams getInstrumentsQueryParams(){
		InstrumentQueryParams instrumentQueryParams = new InstrumentQueryParams();
		instrumentQueryParams.typeCode = typeCode;
		instrumentQueryParams.typeCodes = typeCodes;
		instrumentQueryParams.categoryCode = categoryCode;
		instrumentQueryParams.categoryCodes = categoryCodes;
		instrumentQueryParams.active = active;
		
		return instrumentQueryParams;
	}

}
