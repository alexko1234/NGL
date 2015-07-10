package controllers.migration.models.experiment;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.experiment.instance.ContainerUsed;
import play.Logger;
import validation.ContextValidation;
import validation.utils.ValidationConstants;

import com.fasterxml.jackson.annotation.JsonIgnore;

import controllers.migration.models.ExperimentOld;

public class OneToManyContainerOld extends AtomicTransfertMethodOld {

	public int outputNumber;
	public ContainerUsed inputContainerUsed;
	public List<ContainerUsed> outputContainerUseds;
	
	public OneToManyContainerOld(){
		super();
	}
	
	@Override
	public ContextValidation createOutputContainerUsed(ExperimentOld experiment,ContextValidation contextValidation) {
		Logger.error("Not implemented");
		contextValidation.addErrors("locationOnContainerSupport",ValidationConstants.ERROR_NOTDEFINED_MSG);
		return contextValidation;
	}
	
	@Override
	public ContextValidation saveOutputContainers(ExperimentOld experiment, ContextValidation contextValidation) {
		contextValidation.addErrors("locationOnContainerSupport",ValidationConstants.ERROR_NOTDEFINED_MSG);
		//experiment.outputContainerCodes = experiment.getOutputContainerCodes();
		//ProcessHelper.updateNewContainerSupportCodes
		Logger.error("Not implemented");
		return contextValidation;
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
