package models.laboratory.reporting.instance;

import validation.ContextValidation;
import validation.IValidation;

public class Column implements IValidation{
	
	public String header; //text or message key
	public String property; //angular expression
	public String type; //number, string see datatable
	public Boolean order;
	public Boolean edit;
	public Boolean hide;
	public Boolean group; //active or not the group
	public String groupMethod; //sum, average, distinct
	
	public String format; //number decimal or date format
	public String render; //angular expression
	public String filter; //angular expression
	
	public Boolean choiceInList;
	public String listStyle;
	public String groupBy; //list group by
	
	public String possibleValues;
	public String tdClass;
	
	public String url;
	
	public String[] modes; //chart or table or the two
	public Double position;
	
	public String[] queryIncludeKeys; //if void used property
	
	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
		
	}

}
