package controllers.instruments.api;

import java.util.List;

import controllers.ListForm;

public class InstrumentsSearchForm extends ListForm{
	public String instrumentUsedTypeCode;
	public List<String> instrumentUsedTypeCodes;
	
	public String instrumentCategoryCode;
	public List<String> instrumentCategoryCodes;
	
	public boolean active = true;
}
