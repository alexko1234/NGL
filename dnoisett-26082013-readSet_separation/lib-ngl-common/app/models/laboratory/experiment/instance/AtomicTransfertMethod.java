package models.laboratory.experiment.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.utils.IValidation;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;


@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="class")
@JsonSubTypes({
	@JsonSubTypes.Type(value =  models.laboratory.experiment.instance.ManytoOneContainer.class, name = "ManyToOne"),
	@JsonSubTypes.Type(value =  models.laboratory.experiment.instance.OneToManyContainer.class, name = "OneToMany"),
	@JsonSubTypes.Type(value =  models.laboratory.experiment.instance.OneToOneContainer.class, name = "OneToOne")
})
public abstract class AtomicTransfertMethod implements IValidation {

	public int position;
	public Comment comment;
	public abstract List<Container> createOutputContainerUsed(Experiment experiment);
	@JsonIgnore
	public abstract List<ContainerUsed> getInputContainers();
}
