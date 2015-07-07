package controllers.reagents.api;

import java.util.List;

import controllers.ListForm;

public class BoxSearchForm extends ListForm{
	
	public String code;
	public String kitCode;
	public String barCode;
	public String bundleBarCode;
	public String catalogRefCode;
	
	public String orderCode;
	public Date toExpirationDate;
	public List<String> catalogCodes;
}
