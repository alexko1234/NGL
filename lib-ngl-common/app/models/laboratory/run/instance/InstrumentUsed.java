package models.laboratory.run.instance;

import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentCategoryDAO;

import org.codehaus.jackson.annotate.JsonIgnore;
import validation.ContextValidation;
import validation.IValidation;
import validation.common.instance.CommonValidationHelper;

public class InstrumentUsed implements IValidation {

	// specific class for the run;
	public String code;
	public String categoryCode;


	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		
		//DescriptionValidationHelper.validationInstrumentCode(code,contextValidation);
		//DescriptionValidationHelper.validationInstrumentCategoryCode(code,contextValidation);

		CommonValidationHelper.validateRequiredDescriptionCode(contextValidation, this.code, "code", InstrumentUsedType.find);
		
		contextValidation.addKeyToRootKeyName("categoryCode");
		CommonValidationHelper.validateRequiredDescriptionCode(contextValidation, this.categoryCode, "code", InstrumentCategoryDAO.find);
		contextValidation.removeKeyFromRootKeyName("categoryCode");
		
	}


}
