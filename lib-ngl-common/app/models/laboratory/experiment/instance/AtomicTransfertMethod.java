package models.laboratory.experiment.instance;

import java.util.List;

import models.laboratory.common.instance.Comment;
import models.utils.dao.DAOException;
import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

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

	public List<InputContainerUsed> inputContainerUseds;
	public List<OutputContainerUsed> outputContainerUseds;
	public String line;
	public String column;
	public Comment comment;
	
	public AtomicTransfertMethod() {
		super();
	}
	
	
	public abstract ContextValidation createOutputContainerUsed(Experiment experiment, ContextValidation contextValidation) throws DAOException;
	public abstract ContextValidation saveOutputContainers(Experiment experiment, ContextValidation contextValidation) throws DAOException;
	
	@Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, line, "line");
		ValidationHelper.required(contextValidation, column, "column");
		//AtomicTransfertMethodValidationHelper.validateInputContainers(contextValidation, inputContainerUseds);
		//AtomicTransfertMethodValidationHelper.validateOutputContainers(contextValidation, outputContainerUseds);
		
		
	}
	
	
	
}