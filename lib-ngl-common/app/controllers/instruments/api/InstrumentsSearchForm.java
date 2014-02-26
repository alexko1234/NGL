package controllers.instruments.api;

import java.util.List;

import org.codehaus.jackson.annotate.JsonUnwrapped;

import models.laboratory.instrument.description.InstrumentQueryParams;
import controllers.ListForm;

public class InstrumentsSearchForm extends ListForm {
	
	public String instrumentUsedTypeCode;
	public List<String> instrumentUsedTypeCodes;
	
	public String instrumentCategoryCode;
	public List<String> instrumentCategoryCodes;
	
	public Boolean active;
	
	public InstrumentQueryParams getInstrumentsQueryParams(){
		InstrumentQueryParams instrumentQueryParams = new InstrumentQueryParams();
		instrumentQueryParams.instrumentUsedTypeCode = instrumentUsedTypeCode;
		instrumentQueryParams.instrumentUsedTypeCodes = instrumentUsedTypeCodes;
		instrumentQueryParams.instrumentCategoryCode = instrumentCategoryCode;
		instrumentQueryParams.instrumentCategoryCodes = instrumentCategoryCodes;
		instrumentQueryParams.active = active;
		
		return instrumentQueryParams;
	}

}
