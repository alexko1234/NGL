package models.laboratory.instrument.description;

import java.util.List;

import controllers.ListForm;

public class InstrumentQueryParams {
	
	public String instrumentUsedTypeCode;
	public List<String> instrumentUsedTypeCodes;
	
	public String instrumentCategoryCode;
	public List<String> instrumentCategoryCodes;
	
	public Boolean active;
	
	public boolean isAtLeastOneParam(){
		return (this.instrumentUsedTypeCodes != null || this.instrumentUsedTypeCode != null 
				|| this.instrumentCategoryCode != null || this.instrumentCategoryCodes != null
				|| this.active != null);
	}
}
