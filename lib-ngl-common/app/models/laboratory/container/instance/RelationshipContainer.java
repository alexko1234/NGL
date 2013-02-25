package models.laboratory.container.instance;

import java.util.List;

import models.laboratory.container.description.ContainerCategory;
import models.utils.HelperObjects;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import fr.cea.ig.DBObject;

@MongoCollection(name="RelationshipContainer")
public class RelationshipContainer extends DBObject {

	// container ref
	public String currentContainerCode;
	// containers ref
	public List<String> childContainerCodes;
	
	
	@JsonIgnore
	public ContainerCategory getContainerCategory(){
		return new HelperObjects<ContainerCategory>().getObject(ContainerCategory.class, currentContainerCode, null);

	}
	
	@JsonIgnore
	public List<Container> getChildContainers(){
		return new HelperObjects<Container>().getObjects(Container.class,childContainerCodes);
	}
	
}
