package models.laboratory.project.instance;

/**
 * subset of project with bio-informatic data
 * @author dnoisett
 */

public class BioinformaticParameters {
	
	public Boolean bioinformaticAnalysis = Boolean.FALSE;	
	public String regexBA;
	public String mappingReference;
	
	
	@Override
	public String toString() {
		return "BioinformaticParameters [bioinformaticAnalysis=" + bioinformaticAnalysis
				+ ", regexBA=" + regexBA 
				+ ", mappingReference=" + mappingReference + "]";
	}
	
	
	
}
