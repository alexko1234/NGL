package controllers.sra.experiments.api;
import java.util.List;

import controllers.ListForm;

public class ExperimentsSearchForm  extends ListForm {
	public List<String> listExperimentCodes;
	public String experimentCode;
	public String submissionCode;
	public String studyCode; // ajout pour interface release study
	public String runCode;
}
