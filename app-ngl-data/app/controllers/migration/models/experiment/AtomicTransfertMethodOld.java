package controllers.migration.models.experiment;


import java.util.List;

import models.laboratory.common.instance.Comment;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.utils.dao.DAOException;
import validation.ContextValidation;
import validation.IValidation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import controllers.migration.models.ExperimentOld;


@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="class", defaultImpl= models.laboratory.experiment.instance.OneToOneContainer.class)
@JsonSubTypes({
	@JsonSubTypes.Type(value =  controllers.migration.models.experiment.ManytoOneContainerOld.class, name = "ManyToOne"),
	@JsonSubTypes.Type(value =  controllers.migration.models.experiment.OneToManyContainerOld.class, name = "OneToMany"),
	@JsonSubTypes.Type(value =  controllers.migration.models.experiment.OneToOneContainerOld.class, name = "OneToOne"),
	@JsonSubTypes.Type(value =  controllers.migration.models.experiment.OneToVoidContainerOld.class, name = "OneToVoid")
})
public abstract class AtomicTransfertMethodOld implements IValidation {

	public AtomicTransfertMethodOld() {
		super();
	}
	
	public int position;
	public Comment comment;
	public abstract ContextValidation createOutputContainerUsed(ExperimentOld experiment, ContextValidation contextValidation) throws DAOException;
	@JsonIgnore
	public abstract List<ContainerUsed> getInputContainers();
	public abstract List<ContainerUsed> getOutputContainers();
	
	public abstract ContextValidation saveOutputContainers(ExperimentOld experiment, ContextValidation contextValidation) throws DAOException;
}