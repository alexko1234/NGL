package models.laboratory.container.instance;

import java.util.Map;

import models.laboratory.common.instance.PropertyValue;


/*
 * Embedded data in collection Container
 * When QualityControle is create/update a copy of result are embedded in container
 * 
 * */
public class QualityControlResult {
	
	public String qualityControleCode;
	public String qualityControleTypeCode;
	public Map<String,PropertyValue> properties;
	

	
}
