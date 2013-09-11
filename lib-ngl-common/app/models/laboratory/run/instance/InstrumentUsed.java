package models.laboratory.run.instance;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

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
		
		if(ValidationHelper.required(contextValidation, this, "instrumentUsed")){
			if(ValidationHelper.required(contextValidation, this.code, "instrumentUsed.code")){
				//TODO valid if exist
			}
			if(ValidationHelper.required(contextValidation, this.categoryCode, "instrumentUsed.categoryCode")){
				//TODO valid if exist
			}
		}
	}

}
