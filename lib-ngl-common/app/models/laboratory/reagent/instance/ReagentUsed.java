package models.laboratory.reagent.instance;


import validation.ContextValidation;
import validation.IValidation;


public class ReagentUsed implements IValidation {

	public String kitCatalogCode;//The ref of the kit in the catalog (description)
	
	public String code;//the code of the reagent (instance)
	public String boxCode;//the code of the box (instance)
	
	public String description;
	
	@Override
	public void validate(ContextValidation contextValidation) {
		//DescriptionValidationHelper.validationReagentTypeCode(categoryCode,contextValidation);
	}
}
