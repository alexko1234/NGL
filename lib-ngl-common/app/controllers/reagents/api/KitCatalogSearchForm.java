package controllers.reagents.api;

import java.util.List;

import controllers.ListForm;

public class KitCatalogSearchForm extends ListForm{
	public String code;
	public String name;
	public String catalogRefCode;
	public String providerRefName;
	public String providerCode;
	public List<String> experimentTypeCodes;
	public boolean isActive = true;
}