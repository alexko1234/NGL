package models.laboratory.parameter;

import java.util.Map;

import models.laboratory.common.instance.TraceInformation;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.IValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationHelper;

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
		CommonValidationHelper.validateCode(this, InstanceConstants.PARAMETER_COLL_NAME, contextValidation);
		ValidationHelper.required(contextValidation, categoryCode, "categoryCode");
		ValidationHelper.required(contextValidation, sequence, "sequence");

	}
}



