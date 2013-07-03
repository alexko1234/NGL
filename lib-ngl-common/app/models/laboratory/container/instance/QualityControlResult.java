package models.laboratory.container.instance;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;

import models.laboratory.common.instance.PropertyValue;
import models.utils.IValidation;


/*
 * Embedded data in collection Container
 * When QualityControle is create/update a copy of result are embedded in container
 * 
 * */
public class QualityControlResult implements IValidation {
	
	public String qualityControleCode;
	public String qualityControleTypeCode;
	public Map<String,PropertyValue> properties;
	
	@JsonIgnore
	@Override
	public void validate(Map<String, List<ValidationError>> errors) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean exist(Map<String, List<ValidationError>> errors) {
		return false;
	}
	

	
}
