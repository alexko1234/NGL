package models.laboratory.run.instance;

import static validation.utils.ValidationHelper.required;
import models.utils.IValidation;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.utils.ContextValidation;

public class InstrumentUsed implements IValidation {

	// specific class for the run;
	public String code;
	public String categoryCode;


	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		
		//TODO: Can not be tested now : no description in database.
		//DescriptionValidationHelper.validationInstrumentCode(code,contextValidation);
		//DescriptionValidationHelper.validationInstrumentCategoryCode(code,contextValidation);
		
		if(required(contextValidation.errors, this, "instrumentUsed")){
			if(required(contextValidation.errors, this.code, "instrumentUsed.code")){
				//TODO valid if exist
			}
			if(required(contextValidation.errors, this.categoryCode, "instrumentUsed.categoryCode")){
				//TODO valid if exist
			}
		}
	}

}
