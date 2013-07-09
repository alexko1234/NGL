package models.laboratory.experiment.instance;

import java.util.Map;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.utils.IValidation;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;


@JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
@JsonSubTypes({
	@JsonSubTypes.Type(value = ManytoOneContainer.class, name = "ManyToOne"),
	@JsonSubTypes.Type(value = OneToManyContainer.class, name = "OneToMany"),
	@JsonSubTypes.Type(value = OneToOneContainer.class, name = "OneToOne")
})
public abstract class AtomicTransfertMethod implements IValidation {

	public int position;
	public Comment comment;
	public abstract void createOutputContainerUsed(Map<String,PropertyDefinition> propertyDefinitions, Map<String,PropertyValue> propertyValues);

}
