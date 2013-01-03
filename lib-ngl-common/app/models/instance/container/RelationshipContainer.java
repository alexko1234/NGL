package models.instance.container;

import java.util.List;

import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import utils.refobject.HelperObjects;
import utils.refobject.ObjectMongoDBReference;

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
