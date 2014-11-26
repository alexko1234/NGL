package models.laboratory.parameter;

import java.util.Map;

import models.laboratory.common.instance.TraceInformation;
import validation.ContextValidation;
import validation.IValidation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.cea.ig.DBObject;

public class Index extends DBObject implements IValidation{

	public final String typeCode="indexIllumina"; 
	public String categoryCode;
	public String sequence;
	public Map<String,String> supplierName;
	public TraceInformation traceInformation;
	
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		
	}
}



