package models.laboratory.container.instance;

import java.util.List;

import models.utils.HelperObjects;
import models.utils.ObjectMongoDBReference;
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
	public Container getCurrentContainer(){
		try {
			return new ObjectMongoDBReference<Container>(Container.class, currentContainerCode).getObject();
		} catch (Exception e) {
			// TODO 
		}
		return null;
	}
	
	@JsonIgnore
	public List<Container> getChildContainers(){
		return new HelperObjects<Container>().getObjects(Container.class,childContainerCodes);
	}
	
}
