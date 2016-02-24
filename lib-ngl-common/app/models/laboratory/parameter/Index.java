package models.laboratory.parameter;

import java.util.Map;

import validation.ContextValidation;
import validation.utils.ValidationHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;


public abstract class Index extends Parameter{

	
	public Index(String typeCode) {
		super(typeCode);		
	}

	public String sequence;
	
	public String shortName; //used by NGS-RG
	
	public Map<String,String> supplierName;
	
	
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		super.validate(contextValidation);
		ValidationHelper.required(contextValidation, sequence, "sequence");
		ValidationHelper.required(contextValidation, shortName, "shortName");
	}
}



