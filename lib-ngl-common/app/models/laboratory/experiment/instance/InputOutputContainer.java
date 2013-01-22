package models.laboratory.experiment.instance;

import java.util.List;

import models.laboratory.common.instance.Comment;

public class InputOutputContainer {
	
	// 1 input to n output  or  
	// n input  to 1 output 
	public List<ContainerUsed> inputContainers;
	public List<ContainerUsed> outputContainers;

	public Comment comment;


}
