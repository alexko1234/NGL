package models.laboratory.valuation.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.TraceInformation;

import validation.ContextValidation;
import validation.IValidation;
import fr.cea.ig.DBObject;

public class ValuationCriteria extends DBObject implements IValidation{

	public String name;
	
	public String objectTypeCode;
	
	public List<String> typeCodes;
	
	public List<Property> properties;
	
	public TraceInformation traceInformation;
	
	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
		
	}

}
