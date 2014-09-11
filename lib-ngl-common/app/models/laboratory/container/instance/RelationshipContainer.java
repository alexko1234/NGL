package models.laboratory.container.instance;

import java.util.List;

import models.laboratory.container.description.ContainerCategory;
import models.utils.HelperObjects;
import org.mongojack.MongoCollection;

import com.fasterxml.jackson.annotation.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;



import fr.cea.ig.DBObject;

@MongoCollection(name="RelationshipContainer")
public class RelationshipContainer extends DBObject implements IValidation {

	// container ref
	public String currentContainerCode;
	public String processCode;
	// containers ref
	public List<String> childContainerCodes;


	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
	
	}	
	
}
