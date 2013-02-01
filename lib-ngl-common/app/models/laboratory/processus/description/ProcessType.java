package models.laboratory.processus.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.experiment.description.ExperimentType;

public class ProcessType extends CommonInfoType{

	public ProcessCategory processCategory;
	
	public List<ExperimentType> experimentTypes;
	
}
