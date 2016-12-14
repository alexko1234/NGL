package models.sra.submit.sra.instance;

import java.util.ArrayList;
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
	public Integer baseCoord;
	public String readLabel;
	public List<String> expectedBaseCallTable = new ArrayList<String>(); 
	

	@Override
	public void validate(ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("readSpec");
		ValidationHelper.required(contextValidation, this.readIndex, "readIndex");
		ValidationHelper.required(contextValidation, this.readClass, "readClass");
		ValidationHelper.required(contextValidation, this.readType, "readType");
		if (this.expectedBaseCallTable.size() == 0 ) {
			ValidationHelper.required(contextValidation, this.baseCoord, "baseCoord");
		}
		contextValidation.removeKeyFromRootKeyName("readSpec");
	}

}
