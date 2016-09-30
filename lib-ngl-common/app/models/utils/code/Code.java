package models.utils.code;

import models.laboratory.common.instance.Comment;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.instance.Process;

public interface Code {
	
	
	public String generateContainerSupportCode();
	
	public String generateExperimentCode(Experiment exp);
	
	public String generateExperimentCommentCode(Comment com);
	
	public String generateProcessCode(Process process);

	public String generateSampleCode(String projectCode);
}
