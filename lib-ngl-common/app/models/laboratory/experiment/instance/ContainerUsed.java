package models.laboratory.experiment.instance;

import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Volume;
import models.utils.ObjectMongoDBReference;

import org.codehaus.jackson.annotate.JsonIgnore;


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

