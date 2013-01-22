package models.laboratory.container.instance;

import java.util.Map;

import models.laboratory.common.instance.PropertyValue;


/*
 * Embedded data in collection Container
 * When QualityControle is create/update a copy of result are embedded in container
 * 
 * */
public class QualityControlResult {
	
	public String QualityControleCode;
	public String QualityControleTypeCode;
	public Map<String,PropertyValue> properties;
	

}
