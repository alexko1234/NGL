package models.laboratory.experiment.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.utils.HelperObjects;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.IValidation;
import validation.common.instance.CommonValidationHelper;
import validation.container.ContainerUsedValidation;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class ContainerUsed implements IValidation{
	
	public String code;
	
	public String categoryCode;
	public LocationOnContainerSupport locationOnContainerSupport;
	// Take for inputContainer or Create for outputContainer
	public PropertyValue volume;
	public PropertyValue concentration;
	
	public List<Content> contents;
	
	public Float percentage;
	// Proprietes a renseigner en fonction du type d'experiment ou d'instrument
	public Map<String,PropertyValue> experimentProperties;
	public Map<String,PropertyValue> instrumentProperties;
	
	public State state;
	
	public ContainerUsed() {
		
	}
	
	@JsonIgnore
	public ContainerUsed(String containerCode) {
		this.code=containerCode;
	}
	
	@JsonIgnore
	public ContainerUsed(Container container) {
		this.code = container.code;
		this.volume = container.mesuredVolume;
	}

	@JsonIgnore
	public Container getContainer(){
		return new HelperObjects<Container>().getObject(Container.class, code);
		
	}

	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		if(contextValidation.getObject("stateCode").equals("IP")){
			CommonValidationHelper.validateUniqueInstanceCode(contextValidation, code, Container.class, InstanceConstants.CONTAINER_COLL_NAME);
		}
		else {
			CommonValidationHelper.validateContainerCode(code, contextValidation);
		} 
		if(contextValidation.getObject("typeCode")!=null){
			ContainerUsedValidation.validateExperimentProperties(contextValidation.getObject("typeCode").toString(),experimentProperties,contextValidation);
		}
	}

}

