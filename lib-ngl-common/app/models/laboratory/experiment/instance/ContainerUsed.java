package models.laboratory.experiment.instance;

import java.util.List;
import java.util.Map;
import java.util.Set;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.LocationOnContainerSupport;
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
	public PropertyValue quantity;

	public Set<Content> contents;
	
	public Double percentage;
	// Proprietes a renseigner en fonction du type d'experiment ou d'instrument
	public Map<String,PropertyValue> experimentProperties;
	public Map<String,PropertyValue> instrumentProperties;
	
	public State state;
	
	public Set<String> fromExperimentTypeCodes;
	
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
		this.contents=container.contents;
	}


	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {

		if(contextValidation.getObject("stateCode").equals("IP") && contextValidation.getObject("level") != null && contextValidation.getObject("level").equals(Level.CODE.ContainerOut) ){
			CommonValidationHelper.validateUniqueInstanceCode(contextValidation, code, Container.class, InstanceConstants.CONTAINER_COLL_NAME);
		}
		else if(contextValidation.getObject("stateCode").equals("F")) {
			CommonValidationHelper.validateContainerCode(code, contextValidation);
		} 
		ContainerUsedValidation.validateVolume(contextValidation, volume);
		ContainerUsedValidation.validateConcentration(contextValidation, concentration);
		if(contextValidation.getObject("typeCode")!=null){
			ContainerUsedValidation.validateExperimentProperties(contextValidation.getObject("typeCode").toString(),experimentProperties,contextValidation/*,false*/);//TODO: voir avec Maud
		}		
		
	}

}

