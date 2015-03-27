package controllers.reagents.api;

import java.util.Date;
import java.util.List;

import controllers.ListForm;

public class KitSearchForm extends ListForm{
	public String catalogCode;
	public List<String> catalogCodes;
	
	public String barCode;
	
	public Date startToUseDate;
	public Date stopToUseDate;
	
	public String stateCode;
	
	public String orderCode;
	
	public Date expirationDate;
}
