package models.laboratory.reporting.instance;

import validation.ContextValidation;
import validation.IValidation;

public class Column implements IValidation{
	
	public String header;
	public String property; 
	public String type;
	public Boolean order;
	public Boolean edit;
	public Boolean hide;
	
	public String format;
	public String render; //angular expression
	public String filter; //angular expression
	
	public String choiceInList;
	public String listStyle;
	public String possibleValues;
	public String tdClass;
	public String groupBy;
	
	public String[] modes; //chart or table or the two
	public Integer position;
	
	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
		
	}

}
