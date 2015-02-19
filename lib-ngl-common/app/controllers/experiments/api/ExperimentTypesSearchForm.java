package controllers.experiments.api;

import java.util.List;

import controllers.ListForm;

public class ExperimentTypesSearchForm extends ListForm{
	
	public String categoryCode;
	public List<String> categoryCodes;
	
	public String processTypeCode;
	
	public Boolean withoutOneToVoid;
}
