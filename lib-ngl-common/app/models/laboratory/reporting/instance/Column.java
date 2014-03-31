package models.laboratory.reporting.instance;

import validation.ContextValidation;
import validation.IValidation;

public class Column implements IValidation{
	
	public String header;
	public String property; 
	public String type;
	public Boolean order;
	public String format;
	public String render; //angular expression
	public String filter; //angular expression
	
	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
		
	}

}
