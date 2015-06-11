package models.laboratory.experiment.instance;

import java.util.Set;

import models.laboratory.container.instance.Container;
import models.laboratory.processes.instance.Process;

public class ExperimentUpdateState {

	public String nextStateProcesses;
	public Set<String> processResolutionCodes;
	public String nextStateInputContainers;
	public String nextStateOutputContainers;
	public Set<Process> processes ;
	public Set<Container> inputContainers;
	public Set<Container> outputContainers;
	@Override
	public String toString() {
		return "ExperimentUpdateState [nextStateProcesses="
				+ nextStateProcesses + ", processResolutionCodes="
				+ processResolutionCodes + ", nextStateInputContainers="
				+ nextStateInputContainers + ", nextStateOutputContainers="
				+ nextStateOutputContainers + ", processes=" + processes
				+ ", inputContainers=" + inputContainers
				+ ", outputContainers=" + outputContainers + "]";
	}
	
	
}
