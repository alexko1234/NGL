package models.laboratory.stock.instance;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;

import models.utils.IValidation;

public class StockUsed implements IValidation{
	//Support ref
	public String barCode;
	public String createUser;
	public Date creationDate;
	
	@JsonIgnore
	@Override
	public void validate(Map<String, List<ValidationError>> errors) {
		// TODO Auto-generated method stub
		
	}	

}
