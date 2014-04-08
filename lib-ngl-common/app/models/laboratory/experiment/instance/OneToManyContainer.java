package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.Logger;
import validation.ContextValidation;

public class OneToManyContainer extends AtomicTransfertMethod {

	public int outputNumber;
	public ContainerUsed inputContainerUsed;
	public List<ContainerUsed> outputContainerUseds;
	
	public OneToManyContainer(){
		super();
	}
	
	@Override
	public void createOutputContainerUsed(Experiment experiment) {
		Logger.error("Not implemented");
		
	}
	
	@Override
	public ContextValidation saveOutputContainers(Experiment experiment) {
		Logger.error("Not implemented");
		return null;
	
	}


	@Override
	public void validate(ContextValidation contextValidation) {
		inputContainerUsed.validate(contextValidation);
		for(ContainerUsed containerUsed:outputContainerUseds){
			containerUsed.validate(contextValidation);
		}
	}
	@JsonIgnore
	public List<ContainerUsed> getInputContainers(){
		List<ContainerUsed> cu = new ArrayList<ContainerUsed>();
		cu.add(inputContainerUsed);
		return cu;
	}
	@JsonIgnore
	public List<ContainerUsed> getOutputContainers(){
		return outputContainerUseds;
	}

	
}
