package models.laboratory.run.instance;

import validation.ContextValidation;
import validation.run.instance.ReadSetValidationHelper;

public class ReadSetLane extends ReadSet{

	

	@Override
	public void validate(ContextValidation contextValidation) {
		super.validate(contextValidation);
		ReadSetValidationHelper.validateReadSetCodeInRunLane(this.code, this.runCode, this.laneNumber, contextValidation);
		
	}

	
}