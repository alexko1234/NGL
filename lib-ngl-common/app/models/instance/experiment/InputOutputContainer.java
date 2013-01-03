package models.instance.experiment;

import java.util.List;

import models.instance.common.Comment;

public class InputOutputContainer {
	
	// 1 input to n output  or  
	// n input  to 1 output 
	public List<ContainerUsed> inputContainers;
	public List<ContainerUsed> outputContainers;

	public Comment comment;


}
