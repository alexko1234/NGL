package models.sra.experiment.instance;

import java.util.List;

import models.laboratory.common.instance.TraceInformation;
import validation.ContextValidation;
import validation.IValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationHelper;

public class ReadSpec implements IValidation {
	public int readIndex;
	public String readClass;
	public String readType;
	public Integer lastBaseCoord;
	public String readLabel;
	public List<String> expectedBaseCallTable;
	
	public TraceInformation traceInformation; // new TraceInformation .Reference sur "models.laboratory.common.instance.TraceInformation" 

	@Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, this.readIndex, "readIndex");
		ValidationHelper.required(contextValidation, this.readClass, "readClass");
		ValidationHelper.required(contextValidation, this.readType, "readType");
		ValidationHelper.required(contextValidation, this.lastBaseCoord, "lastBaseCoord");
		CommonValidationHelper.validateTraceInformation(traceInformation, contextValidation);
	}


}
