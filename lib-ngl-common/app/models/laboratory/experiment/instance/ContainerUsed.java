package models.laboratory.experiment.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.utils.HelperObjects;
import models.utils.IValidation;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;


public class ContainerUsed implements IValidation{
	
	public String containerCode;
	// Take for inputContainer or Create for outputContainer
	public PropertyValue volume;
	// Proprietes a renseigner en fonction du type d'experiment ou d'instrument
	public Map<String,PropertyValue> experimentProperties;
	public Map<String,PropertyValue> instrumentProperties;
	
	@JsonIgnore
	public Container getContainer(){
		return new HelperObjects<Container>().getObject(Container.class, containerCode);
		
	}

	@JsonIgnore
	@Override
	public void validate(Map<String, List<ValidationError>> errors) {
		// TODO Auto-generated method stub
		
	}

}

