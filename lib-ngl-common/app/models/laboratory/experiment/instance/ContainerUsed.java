package models.laboratory.experiment.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.utils.HelperObjects;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import validation.common.instance.CommonValidationHelper;


public class ContainerUsed implements IValidation{
	
	public String containerCode;
	// Take for inputContainer or Create for outputContainer
	public PropertyValue volume;
	
	public Float percentage;
	// Proprietes a renseigner en fonction du type d'experiment ou d'instrument
	public Map<String,PropertyValue> experimentProperties;
	public Map<String,PropertyValue> instrumentProperties;
	
	public List<String> resolutionCodes;
	
	public ContainerUsed() {
		
	}
	
	@JsonIgnore
	public ContainerUsed(String containerCode) {
		this.containerCode=containerCode;
	}
	
	@JsonIgnore
	public ContainerUsed(Container container) {
		this.containerCode = container.code;
		this.volume = container.mesuredVolume;
	}

	@JsonIgnore
	public Container getContainer(){
		return new HelperObjects<Container>().getObject(Container.class, containerCode);
		
	}

	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		CommonValidationHelper.validateContainerCode(containerCode, contextValidation);
		//TODO validate experimentProperties ?? 
		//TODO validate instrumentProperties ??
	}

}

