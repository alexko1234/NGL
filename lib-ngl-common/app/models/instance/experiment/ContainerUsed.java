package models.instance.experiment;

import java.util.Map;

import models.instance.common.PropertyValue;
import models.instance.container.Container;
import models.instance.container.Volume;

import org.codehaus.jackson.annotate.JsonIgnore;

import utils.refobject.ObjectMongoDBReference;

public class ContainerUsed {
	
	public String containerCode;
	// Take for inputContainer or Create for outputContainer
	public Volume volume;
	// Proprietes a renseigner en fonction du type d'experiment ou d'instrument
	Map<String,PropertyValue> experimentProperties;
	Map<String,PropertyValue> instrumentProperties;
	
	@JsonIgnore
	public Container getContainer(){
		try {
			return new ObjectMongoDBReference<Container>(Container.class, containerCode).getObject();
		} catch (Exception e) {
			// TODO
		}
		return null;
		
	}
}

