package models.laboratory.project.instance;

/**
 * subset of project
 * @author dnoisett
 *
 */
public class BioinformaticParameters {
	
	public Boolean bioinformaticAnalysis = Boolean.FALSE;	
	public String regexBA;
	public String mappingReference;
	
	public BioinformaticParameters() {
		this.bioinformaticAnalysis = Boolean.FALSE;
		this.regexBA = null;
		this.mappingReference = null;
	}
	
	public BioinformaticParameters(Boolean bioinformaticAnalysis) {
		this.bioinformaticAnalysis = bioinformaticAnalysis;
		this.regexBA = null;
		this.mappingReference = null;
	}
	
	
	
	@Override
	public String toString() {
		return "BioinformaticParameters [bioinformaticAnalysis=" + bioinformaticAnalysis
				+ ", regexBA=" + regexBA 
				+ ", mappingReference=" + mappingReference + "]";
	}
	
	
}
