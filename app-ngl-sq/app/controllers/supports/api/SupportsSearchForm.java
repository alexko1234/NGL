package controllers.supports.api;

import controllers.ListForm;

public class SupportsSearchForm extends ListForm {
	public String categoryCode;
	public String stateCode;
	public String experimentTypeCode;
	
	@Override
	public String toString() {
		return "SupportsSearchForm[categoryCode = "+categoryCode+", stateCode="+stateCode+", experimentTypeCode="+experimentTypeCode+"]";
	}
}
