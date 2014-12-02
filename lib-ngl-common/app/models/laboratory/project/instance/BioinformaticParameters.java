package models.laboratory.project.instance;

import validation.ContextValidation;
import validation.IValidation;

/**
 * subset of project with bio-informatic data
 * @author dnoisett
 */

public class BioinformaticParameters implements IValidation {
	
	public Boolean biologicalAnalysis = Boolean.FALSE;	
	public String regexBiologicalAnalysis;
	public String mappingReference;
	
	
	@Override
	public String toString() {
		return "BioinformaticParameters [biologicalAnalysis=" + biologicalAnalysis
				+ ", regexBiologicalAnalysis=" + regexBiologicalAnalysis 
				+ ", mappingReference=" + mappingReference + "]";
	}


	@Override
	public void validate(ContextValidation contextValidation) {
		
	}
	
	
	
}
