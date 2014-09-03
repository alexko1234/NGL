package models.laboratory.project.instance;

/**
 * subset of project with bio-informatic data
 * @author dnoisett
 */

public class BioinformaticParameters {
	
	public Boolean biologicalAnalysis = Boolean.FALSE;	
	public String regexBiologicalAnalysis;
	public String mappingReference;
	
	
	@Override
	public String toString() {
		return "BioinformaticParameters [biologicalAnalysis=" + biologicalAnalysis
				+ ", regexBiologicalAnalysis=" + regexBiologicalAnalysis 
				+ ", mappingReference=" + mappingReference + "]";
	}
	
	
	
}
