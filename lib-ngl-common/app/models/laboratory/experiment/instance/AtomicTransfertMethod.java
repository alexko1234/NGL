package models.laboratory.experiment.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.utils.dao.DAOException;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

import validation.ContextValidation;
import validation.IValidation;


@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="class", defaultImpl= models.laboratory.experiment.instance.OneToOneContainer.class)
@JsonSubTypes({
	@JsonSubTypes.Type(value =  models.laboratory.experiment.instance.ManytoOneContainer.class, name = "ManyToOne"),
	@JsonSubTypes.Type(value =  models.laboratory.experiment.instance.OneToManyContainer.class, name = "OneToMany"),
	@JsonSubTypes.Type(value =  models.laboratory.experiment.instance.OneToOneContainer.class, name = "OneToOne"),
	@JsonSubTypes.Type(value =  models.laboratory.experiment.instance.OneToVoidContainer.class, name = "OneToVoid")
})
public abstract class AtomicTransfertMethod implements IValidation {

	public AtomicTransfertMethod() {
		super();
	}
	
	public int position;
	public Comment comment;
	public abstract void createOutputContainerUsed(Experiment experiment) throws DAOException;
	@JsonIgnore
	public abstract List<ContainerUsed> getInputContainers();
	public abstract List<ContainerUsed> getOutputContainers();
	
	public abstract ContextValidation saveOutputContainers(Experiment experiment);
}
