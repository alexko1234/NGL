package models.laboratory.experiment.instance;

import java.util.List;

import models.laboratory.common.instance.Comment;
import models.laboratory.container.description.ContainerSupportCategory;
import play.Logger;
import validation.ContextValidation;
import validation.IValidation;
import validation.experiment.instance.AtomicTransfertMethodValidationHelper;
import validation.utils.ValidationHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;


@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="class", defaultImpl= models.laboratory.experiment.instance.OneToOneContainer.class)
@JsonSubTypes({
	@JsonSubTypes.Type(value =  models.laboratory.experiment.instance.ManyToOneContainer.class, name = "ManyToOne"),
	@JsonSubTypes.Type(value =  models.laboratory.experiment.instance.OneToManyContainer.class, name = "OneToMany"),
	@JsonSubTypes.Type(value =  models.laboratory.experiment.instance.OneToOneContainer.class, name = "OneToOne"),
	@JsonSubTypes.Type(value =  models.laboratory.experiment.instance.OneToVoidContainer.class, name = "OneToVoid")
})
public abstract class AtomicTransfertMethod implements IValidation {

	public Integer viewIndex; //use in rules validation to have the position+1 in the list of ATM.	
	public List<InputContainerUsed> inputContainerUseds;
	public List<OutputContainerUsed> outputContainerUseds;
	public String line; //is equal to outputSupportContainerLine
	public String column; //is equal to outputSupportContainerColumn
	public Comment comment;
	
	public AtomicTransfertMethod() {
//		super();
	}
	
	public abstract void updateOutputCodeIfNeeded(ContainerSupportCategory outputCsc, String supportCode);
	
	public abstract void removeOutputContainerCode() ;
	
	@Override
	public void validate(ContextValidation contextValidation) {
//		long t0 = System.currentTimeMillis();
		AtomicTransfertMethodValidationHelper.validationLineAndColumn(contextValidation,line,column);
//		long t1 = System.currentTimeMillis();
		AtomicTransfertMethodValidationHelper.validateInputContainers(contextValidation, inputContainerUseds);
//		long t2 = System.currentTimeMillis();
		/*
		Logger.debug("ATM validate \n "
				+"1 = "+(t1-t0)+" ms\n"
				+"2 = "+(t2-t1)+" ms\n"
				+"3 = "+(t2-t0)+" ms\n"
				
				);
				
		*/
	}
	
}
