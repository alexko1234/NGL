package models.laboratory.experiment.instance;

import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Volume;
import models.utils.HelperObjects;

import org.codehaus.jackson.annotate.JsonIgnore;


public class ContainerUsed {
	
	public String containerCode;
	// Take for inputContainer or Create for outputContainer
	public Volume volume;
	// Proprietes a renseigner en fonction du type d'experiment ou d'instrument
	public Map<String,PropertyValue> experimentProperties;
	public Map<String,PropertyValue> instrumentProperties;
	
	@JsonIgnore
	public Container getContainer(){
		return new HelperObjects<Container>().getObject(Container.class, containerCode, null);
		
	}
}

