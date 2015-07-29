package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.Level;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.Logger;
import validation.ContextValidation;
import validation.experiment.instance.AtomicTransfertMethodValidationHelper;
import validation.utils.ValidationConstants;

public class OneToManyContainer extends AtomicTransfertMethod {

	public int outputNumber;
	
	public OneToManyContainer(){
		super();
	}
	
	@Override
	public ContextValidation createOutputContainerUsed(Experiment experiment,ContextValidation contextValidation) {
		Logger.error("Not implemented");
		contextValidation.addErrors("locationOnContainerSupport",ValidationConstants.ERROR_NOTDEFINED_MSG);
		return contextValidation;
	}
	
	@Override
	public ContextValidation saveOutputContainers(Experiment experiment, ContextValidation contextValidation) {
		contextValidation.addErrors("locationOnContainerSupport",ValidationConstants.ERROR_NOTDEFINED_MSG);
		//experiment.outputContainerCodes = experiment.getOutputContainerCodes();
		//ProcessHelper.updateNewContainerSupportCodes
		Logger.error("Not implemented");
		return contextValidation;
	}


	@Override
	public void validate(ContextValidation contextValidation) {
		if(CollectionUtils.isNotEmpty(outputContainerUseds)){
			contextValidation.putObject("level", Level.CODE.ContainerOut);
			contextValidation.addKeyToRootKeyName("outputContainerUsed");
			for(ContainerUsed containerUsed:outputContainerUseds){
				containerUsed.validate(contextValidation);
			}
			contextValidation.removeKeyFromRootKeyName("outputContainerUsed");
			contextValidation.removeObject("level");
		}
		
		contextValidation.addKeyToRootKeyName("inputContainerUseds");
		contextValidation.putObject("level", Level.CODE.ContainerIn);
		inputContainerUseds.get(0).validate(contextValidation);
		contextValidation.removeObject("level");
		contextValidation.removeKeyFromRootKeyName("inputContainerUseds");
		
		AtomicTransfertMethodValidationHelper.validateOneInputContainer(inputContainerUseds, contextValidation);
		
	}
	@JsonIgnore
	public List<ContainerUsed> getInputContainers(){			
		return inputContainerUseds;
	}
	@JsonIgnore
	public List<ContainerUsed> getOutputContainers(){
		return outputContainerUseds;
	}

	
}
