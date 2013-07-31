package models.laboratory.container.instance;

import java.util.List;

import models.laboratory.container.description.ContainerCategory;
import models.utils.HelperObjects;
import models.utils.IValidation;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.utils.ContextValidation;



import fr.cea.ig.DBObject;

@MongoCollection(name="RelationshipContainer")
public class RelationshipContainer extends DBObject implements IValidation {

	// container ref
	public String currentContainerCode;
	public String processCode;
	// containers ref
	public List<String> childContainerCodes;
	
	
	@JsonIgnore
	public ContainerCategory getContainerCategory(){
		return new HelperObjects<ContainerCategory>().getObject(ContainerCategory.class, currentContainerCode);

	}
	
	@JsonIgnore
	public List<Container> getChildContainers(){
		return new HelperObjects<Container>().getObjects(Container.class,childContainerCodes);
	}
	
	
	@JsonIgnore
	public Process getProcess(){
		return new HelperObjects<Process>().getObject(Process.class, processCode);

	}

	@JsonIgnore
	@Override
	public void validate(ContextValidation contextErrors) {
		// TODO Auto-generated method stub
	
	}	
	
}
