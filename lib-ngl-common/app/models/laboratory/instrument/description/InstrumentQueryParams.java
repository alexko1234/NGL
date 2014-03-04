package models.laboratory.instrument.description;

import java.util.List;

import controllers.ListForm;

public class InstrumentQueryParams {
	
	public String typeCode;
	public List<String> typeCodes;
	
	public String categoryCode;
	public List<String> categoryCodes;
	
	public Boolean active;
	
	public boolean isAtLeastOneParam(){
		return (this.typeCodes != null || this.typeCode != null 
				|| this.categoryCode != null || this.categoryCodes != null
				|| this.active != null);
	}
}
