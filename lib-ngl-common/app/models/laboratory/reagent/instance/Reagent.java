package models.laboratory.reagent.instance;

import validation.ContextValidation;

public class Reagent extends AbstractDeclaration {
	
	public String catalogCode;
	public String boxCode;
	
	public int stockNumber;

	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
		
	}
	
}
