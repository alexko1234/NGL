package models.laboratory.experiment.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.LocationOnContainerSupport;
import validation.IValidation;

public abstract class AbstractContainerUsed implements IValidation {
	public String code;
	public String categoryCode;
	public List<Content> contents;
	public LocationOnContainerSupport locationOnContainerSupport;
	
	public PropertySingleValue volume;        
	public PropertySingleValue concentration; 
	public PropertySingleValue quantity; 	
	public PropertySingleValue size; 	
	
	public Map<String,PropertyValue<?>> experimentProperties;
	public Map<String,PropertyValue<?>> instrumentProperties;
	
	public AbstractContainerUsed() {
		super();
		
	}
	
	public AbstractContainerUsed(String code) {
		this.code=code;
	}
	
}
