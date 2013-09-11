package models.laboratory.stock.instance;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;




public class StockUsed implements IValidation{
	//Support ref
	public String barCode;
	public String createUser;
	public Date creationDate;
	
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
		
	}

}
