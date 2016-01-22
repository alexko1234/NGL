package models.laboratory.processes.description;

import models.laboratory.experiment.description.ExperimentType;

public class ProcessExperimentType {
	public ExperimentType experimentType;
	public Integer positionInProcess;
	
	
	
	public ProcessExperimentType() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProcessExperimentType(ExperimentType experimentType,
			Integer processOrder) {
		super();
		this.experimentType = experimentType;
		this.positionInProcess = processOrder;
	}
	
	
	
}
