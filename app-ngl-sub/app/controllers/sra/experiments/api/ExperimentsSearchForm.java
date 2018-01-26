package controllers.sra.experiments.api;
import java.util.ArrayList;
import java.util.List;

import controllers.ListForm;

public class ExperimentsSearchForm  extends ListForm {
	public String code;
	public List<String> codes; //remplacé par codes
	public String codeRegex;
	public String submissionCode;
	public String studyCode; // ajout pour interface release study
	public String runCode;
	public List<String> projCodes = new ArrayList<String>();
	public String stateCode;
	public List<String> stateCodes = new ArrayList<String>();
	public String accession;
	public List<String> accessions = new ArrayList<String>();
	public String accessionRegex;
}