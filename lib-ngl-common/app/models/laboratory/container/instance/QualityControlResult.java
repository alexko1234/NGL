package models.laboratory.container.instance;

import java.util.Date;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.Valuation;


/*
 * Embedded data in collection Container
 * When QualityControle is create/update a copy of result are embedded in container
 * 
 * */
public class QualityControlResult implements IValidation {
	
	public Integer index;
	public String code;
	public String typeCode;
	public Date date;
	public Valuation valuation;
	public Map<String,PropertyValue> properties;
	
	public QualityControlResult(){
		
	}
	
	public QualityControlResult(String code, String typeCode, Integer index, Map<String,PropertyValue> expProperties, Map<String,PropertyValue> instProperties, Valuation valuation) {
		this.index = index;
		this.code = code;
		this.typeCode = typeCode;
		this.date = new Date();
		this.properties = expProperties;
		if(MapUtils.isNotEmpty(this.properties) && MapUtils.isNotEmpty(instProperties))
			this.properties.putAll(instProperties); 
		else if(MapUtils.isNotEmpty(instProperties)){
			this.properties = instProperties;
		}
		this.valuation = valuation;
		
	}

	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		
	}
	
}
